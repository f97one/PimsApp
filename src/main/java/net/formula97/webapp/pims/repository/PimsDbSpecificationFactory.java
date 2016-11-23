package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;

/**
 * Spring Data JPAのSpecificationを、ちょっと汎用的に扱えるようにしたSpecification組立クラス。<br />
 * Created by f97one on 2016/11/19.
 */
public class PimsDbSpecificationFactory<T> {

    private final Class<T> mClass;

    /**
     * コンストラクタ、対象クラスをとる。
     *
     * @param clazz 対象クラス
     */
    public PimsDbSpecificationFactory(Class<T> clazz) {
        this.mClass = clazz;
    }

    private boolean isValidFieldSpecified(String fieldName) {
        boolean ret;

        try {
            Class<?> c = mClass.newInstance().getClass();
            Field f = c.getDeclaredField(fieldName);

            ret = true;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();

            ret = false;
        }

        return ret;
    }

    /**
     * <pre>FIELD_NAME LIKE '%fieldVal%'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return likeを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> contains(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.like(root.get(fieldName), "%" + fieldVal.toString() + "%");
    }

    /**
     * <pre>FIELD_NAME NOT LIKE '%fieldVal%'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return notLikeを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> notContains(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.notLike(root.get(fieldName), "%" + fieldVal.toString() + "%");
    }

    /**
     * <pre>FIELD_NAME = 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return equalを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> eq(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.equal(root.get(fieldName), fieldVal);
    }

    /**
     * <pre>FIELD_NAME <> 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return notEqualを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> neq(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.notEqual(root.get(fieldName), fieldVal);
    }

    /**
     * <pre>FIELD_NAME LIKE 'fieldVal%'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return likeを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> startsWith(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.like(root.get(fieldName), fieldVal.toString() + "%");
    }

    /**
     * <pre>FIELD_NAME LIKE '%fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return likeを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> endsWith(String fieldName, Object fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.like(root.get(fieldName), "%" + fieldVal.toString());
    }

    /**
     * <pre>FIELD_NAME > 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return gtを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> gt(String fieldName, Number fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.gt(root.get(fieldName), fieldVal);
    }

    /**
     * <pre>FIELD_NAME < 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return ltを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> lt(String fieldName, Number fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.lt(root.get(fieldName), fieldVal);
    }

    /**
     * <pre>FIELD_NAME >= 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return geを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> ge(String fieldName, Number fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.ge(root.get(fieldName), fieldVal);
    }

    /**
     * <pre>FIELD_NAME <= 'fieldVal'</pre> に相当するJPQLを生成するSpecificationを返す。
     *
     * @param fieldName 対象フィールド名
     * @param fieldVal  検索値
     * @return leを処理するSpecification、fieldValにnullを指定したときはnull
     */
    public Specification<T> le(String fieldName, Number fieldVal) {
        if (!isValidFieldSpecified(fieldName)) {
            throw new IllegalArgumentException("Object does not have field : " + fieldName);
        }

        return fieldVal == null ? null : (root, query, cb) -> cb.le(root.get(fieldName), fieldVal);
    }

}
