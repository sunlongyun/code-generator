package ${cfg.examplePath};

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
/**
* <p>
* ${table.comment!}
* </p>
*
* @author ${author}
* @date ${date}
*/
@Data
<#if table.convert>
@TableName("${table.name}")
</#if>
public class ${entity}Example implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ${entity}Example(){oredCriteria = new ArrayList<>();}

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }


     protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;
        protected GeneratedCriteria() {super();criteria = new ArrayList<Criterion>();}

        public boolean isValid() {return criteria.size() > 0;}

        public List<Criterion> getAllCriteria() {return criteria;}

        public List<Criterion> getCriteria() {return criteria;}

        protected void addCriterion(String condition) {
            if (condition == null) {throw new RuntimeException("Value for condition cannot be null");}
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {

            if (value == null) {throw new RuntimeException("Value for " + property + " cannot be null");}
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        /**
        *
        * 循环写参数
        * @return
        */
        <#list table.fields as field>
        // in,   not in
        public Criteria and${field.capitalName}In(List<${field.propertyType}> values) {
            addCriterion("${field.name} in", values, "${field.propertyName}");
            return (Criteria) this;
        }
        public Criteria and${field.capitalName}NotIn(List<${field.propertyType}> values) {
            addCriterion("${field.name} not in", values, "${field.propertyName}");
            return (Criteria) this;
        }

        //between
        public Criteria and${field.capitalName}Between(${field.propertyType} value1, ${field.propertyType} value2) {
            addCriterion("${field.name} between", value1, value2, "${field.propertyName}");
            return (Criteria) this;
        }

        //like
        public Criteria and${field.capitalName}Like(String value) {
            addCriterion("${field.name} like", value, "${field.propertyName}");
            return (Criteria) this;
        }

        // = <>
        public Criteria and${field.capitalName}EqualTo(${field.propertyType} value) {
            addCriterion("${field.name} =", value, "${field.propertyName}");
            return (Criteria) this;
        }
        public Criteria and${field.capitalName}NotEqualTo(${field.propertyType} value) {
            addCriterion("${field.name} <>", value, "${field.propertyName}");
            return (Criteria) this;
        }


        // > >= < <=
        public Criteria and${field.capitalName}GreaterThan(${field.propertyType} value) {
            addCriterion("${field.name} >", value, "${field.propertyName}");
            return (Criteria) this;
        }
        public Criteria and${field.capitalName}GreaterThanOrEqualTo(${field.propertyType} value) {
            addCriterion("${field.name} >=", value, "${field.propertyName}");
            return (Criteria) this;
        }
        public Criteria and${field.capitalName}LessThan(${field.propertyType} value) {
            addCriterion("${field.name} <", value, "${field.propertyName}");
            return (Criteria) this;
        }
        public Criteria and${field.capitalName}LessThanOrEqualTo(${field.propertyType} value) {
            addCriterion("${field.name} <=", value, "${field.propertyName}");
            return (Criteria) this;
        }

        //null  not null
        public Criteria and${field.capitalName}IsNull() {
            addCriterion("${field.name} is null");
            return (Criteria) this;
        }

        public Criteria and${field.capitalName}IsNotNull() {
            addCriterion("${field.name} is not null");
            return (Criteria) this;
        }
        </#list>
     }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
    public static class Criterion {

        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
             } else {
                this.singleValue = true;
             }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
           this(condition, value, secondValue, null);
        }


     }
}