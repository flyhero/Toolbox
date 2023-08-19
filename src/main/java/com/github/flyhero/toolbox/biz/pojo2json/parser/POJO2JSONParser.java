package com.github.flyhero.toolbox.biz.pojo2json.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.flyhero.toolbox.biz.pojo2json.parser.type.*;
import com.google.gson.GsonBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiUtil;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UVariable;

public abstract class POJO2JSONParser {


	private final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

	private final Map<String, SpecifyType> specifyTypes = new HashMap<>();

	private final List<String> iterableTypes = List.of(
			"Iterable",
			"Collection",
			"List",
			"Set");

	public POJO2JSONParser() {

		DecimalType decimalType = new DecimalType();
		LocalDateTimeType localDateTimeType = new LocalDateTimeType();
		ObjectType objectType = new ObjectType();

		specifyTypes.put("Boolean", new BooleanType());
		specifyTypes.put("Float", decimalType);
		specifyTypes.put("Double", decimalType);
		specifyTypes.put("BigDecimal", decimalType);
		specifyTypes.put("Number", new IntegerType());
		specifyTypes.put("Character", new CharType());
		specifyTypes.put("CharSequence", new StringType());
		specifyTypes.put("Date", localDateTimeType);
		specifyTypes.put("Temporal", new TemporalType());
		specifyTypes.put("LocalDateTime", localDateTimeType);
		specifyTypes.put("LocalDate", new LocalDateType());
		specifyTypes.put("LocalTime", new LocalTimeType());
		specifyTypes.put("ZonedDateTime", new ZonedDateTimeType());
		specifyTypes.put("YearMonth", new YearMonthType());
		specifyTypes.put("UUID", new UUIDType());
		specifyTypes.put("JsonNode", objectType);
		specifyTypes.put("ObjectNode", objectType);
		specifyTypes.put("ArrayNode", new ArrayType());
	}

	protected abstract Object getFakeValue(SpecifyType specifyType);

	public String uElementToJSONString(@NotNull final UElement uElement) {

		Object result = null;

		if (uElement instanceof UVariable) {
			PsiType type = ((UVariable) uElement).getType();
			result = parseFieldValueType(type, 0, List.of(), getPsiClassGenerics(type));
		} else if (uElement instanceof UClass) {
			// UClass.getJavaPsi() IDEA 21* and last version recommend
			result = parseClass(((UClass) uElement).getJavaPsi(), 0, List.of(), Map.of());
		}

		return gsonBuilder.create().toJson(result);
	}

	private Map<String, Object> parseClass(PsiClass psiClass, int level, List<String> ignoreProperties, Map<String, PsiType> psiClassGenerics) {
		PsiAnnotation annotation = psiClass.getAnnotation(com.fasterxml.jackson.annotation.JsonIgnoreType.class.getName());
		if (annotation != null) {
			return null;
		}
		return Arrays.stream(psiClass.getAllFields())
				.map(field -> parseField(field, level, ignoreProperties, psiClassGenerics))
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (ov, nv) -> ov, LinkedHashMap::new));
	}


	private Map.Entry<String, Object> parseField(PsiField field, int level, List<String> ignoreProperties, Map<String, PsiType> psiClassGenerics) {
		// 移除所有 static 属性，这其中包括 kotlin 中的 companion object 和 INSTANCE
		if (field.hasModifierProperty(PsiModifier.STATIC)) {
			return null;
		}
		if (field.hasModifierProperty(PsiModifier.TRANSIENT)) {
			return null;
		}
		if (ignoreProperties.contains(field.getName())) {
			return null;
		}

		PsiAnnotation annotation = field.getAnnotation(com.fasterxml.jackson.annotation.JsonIgnore.class.getName());
		if (annotation != null) {
			return null;
		}

		PsiDocComment docComment = field.getDocComment();
		if (docComment != null) {
			PsiDocTag psiDocTag = docComment.findTagByName("JsonIgnore");
			if (psiDocTag != null && "JsonIgnore".equals(psiDocTag.getName())) {
				return null;
			}

			ignoreProperties = POJO2JSONParserUtils.docTextToList("@JsonIgnoreProperties", docComment.getText());
		} else {
			annotation = field.getAnnotation(com.fasterxml.jackson.annotation.JsonIgnoreProperties.class.getName());
			if (annotation != null) {
				ignoreProperties = POJO2JSONParserUtils.arrayTextToList(annotation.findAttributeValue("value").getText());
			}
		}

		String fieldKey = parseFieldKey(field);
		if (fieldKey == null) {
			return null;
		}
		Object fieldValue = parseFieldValue(field, level, ignoreProperties, psiClassGenerics);
		if (fieldValue == null) {
			return null;
		}
		return Map.entry(fieldKey, fieldValue);
	}

	private String parseFieldKey(PsiField field) {

		PsiAnnotation annotation = field.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class.getName());
		if (annotation != null) {
			String fieldName = POJO2JSONParserUtils.psiTextToString(annotation.findAttributeValue("value").getText());
			if (StringUtils.isNotBlank(fieldName)) {
				return fieldName;
			}
		}

		annotation = field.getAnnotation("com.alibaba.fastjson.annotation.JSONField");
		if (annotation != null) {
			String fieldName = POJO2JSONParserUtils.psiTextToString(annotation.findAttributeValue("name").getText());
			if (StringUtils.isNotBlank(fieldName)) {
				return fieldName;
			}
		}
		return field.getName();
	}

	private Object parseFieldValue(PsiField field, int level, List<String> ignoreProperties, Map<String, PsiType> psiClassGenerics) {
		return parseFieldValueType(field.getType(), level, ignoreProperties, psiClassGenerics);
	}

	/**
	 * PsiType 转换为特定 Object
	 *
	 * @param type             PsiType
	 * @param level            当前转换层级。当递归层级过深时会导致stack overflow，这个参数用于控制递归层级
	 * @param ignoreProperties 过滤的属性，这个参数只在这里使用 {@link com.github.flyhero.toolbox.biz.pojo2json.parser.POJO2JSONParser#parseField}
	 *                         用于过滤用户指定移除的属性
	 * @param psiClassGenerics 当前PsiType的Class所拥有的泛型Map，Map中包含当前PsiClass所定义的 泛型 和 泛型对应的用户指定类型 (E=CustomObject)
	 *                         并在解析当前PsiClass所包含的Field时，尝试获取这个Field所定义的泛型Map，然后传入下一层
	 * @return JSON Value所期望的Object
	 */
	private Object parseFieldValueType(PsiType type,
									   int level,
									   List<String> ignoreProperties,
									   Map<String, PsiType> psiClassGenerics) {

		level = ++level;

		if (type instanceof PsiPrimitiveType) {       //primitive Type

			return getPrimitiveTypeValue(type);

		} else if (type instanceof PsiArrayType) {   //array type

			PsiType deepType = type.getDeepComponentType();
			Object obj = parseFieldValueType(deepType, level, ignoreProperties, getPsiClassGenerics(deepType));
			return obj != null ? List.of(obj) : List.of();

		} else {    //reference Type

			PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);

			if (psiClass == null) {
				return new LinkedHashMap<>();
			}

			if (psiClass.isEnum()) { // enum

				return this.getFakeValue(new EnumType(psiClass));

			} else {

				List<String> fieldTypeNames = new ArrayList<>();

				fieldTypeNames.add(psiClass.getName());
				fieldTypeNames.addAll(Arrays.stream(psiClass.getSupers())
						.map(PsiClass::getName).collect(Collectors.toList()));


				List<String> retain = new ArrayList<>(fieldTypeNames);
				retain.retainAll(specifyTypes.keySet());
				if (!retain.isEmpty()) {  // Object Test<String,String>
					return this.getFakeValue(specifyTypes.get(retain.get(0)));
				} else {

					boolean iterable = fieldTypeNames.stream().anyMatch(iterableTypes::contains);

					if (iterable) {// Iterable List<Test<String>>

						PsiType deepType = PsiUtil.extractIterableTypeParameter(type, false);
						Object obj = parseFieldValueType(deepType, level, ignoreProperties, getPsiClassGenerics(deepType));
						return obj != null ? List.of(obj) : List.of();

					} else {

						if (level > 500) {
							throw new KnownException("This class reference level exceeds maximum limit or has nested references!");
						}

						PsiType deepType = psiClassGenerics.get(psiClass.getName());
						if (deepType != null) {
							return parseFieldValueType(deepType, level, ignoreProperties, getPsiClassGenerics(deepType));
						}

						return parseClass(psiClass, level, ignoreProperties, getPsiClassGenerics(type));
					}
				}

			}
		}
	}

	private Map<String, PsiType> getPsiClassGenerics(PsiType type) {
		PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
		if (psiClass != null) {
			return Arrays.stream(psiClass.getTypeParameters())
					.collect(Collectors.toMap(NavigationItem::getName,
							p -> PsiUtil.substituteTypeParameter(type, psiClass, p.getIndex(), false)));
		}
		return Map.of();
	}

	private Object getPrimitiveTypeValue(PsiType type) {
		switch (type.getCanonicalText()) {
			case "boolean":
				return this.getFakeValue(specifyTypes.get("Boolean"));
			case "byte":
			case "short":
			case "int":
			case "long":
				return this.getFakeValue(specifyTypes.get("Number"));
			case "float":
			case "double":
				return this.getFakeValue(specifyTypes.get("BigDecimal"));
			case "char":
				return this.getFakeValue(specifyTypes.get("Character"));
			default:
				return null;
		}
	}

}
