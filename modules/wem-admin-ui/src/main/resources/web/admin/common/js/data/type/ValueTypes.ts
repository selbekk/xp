module api.data.type {

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new ValueType("Data");

        static STRING = new ValueType("String");

        static HTML_PART = new ValueType("HtmlPart");

        static XML = new ValueType("Xml");

        static LOCAL_DATE = new LocalDateValueType();

        static DATE_TIME = new ValueType("DateTime");

        static CONTENT_ID = new ContentIdValueType("ContentId");

        static LONG = new LongValueType();

        static BOOLEAN = new BooleanValueType();

        static DOUBLE = new ValueType("Double");

        static GEO_POINT = new ValueType("GeoPoint");

        static ENTITY_ID = new ValueType("EntityId");

        static ALL: ValueType[] = [
            ValueTypes.DATA,
            ValueTypes.STRING,
            ValueTypes.HTML_PART,
            ValueTypes.XML,
            ValueTypes.LOCAL_DATE,
            ValueTypes.DATE_TIME,
            ValueTypes.CONTENT_ID,
            ValueTypes.LONG,
            ValueTypes.BOOLEAN,
            ValueTypes.DOUBLE,
            ValueTypes.GEO_POINT,
            ValueTypes.ENTITY_ID];

        public static fromName(name: string): ValueType {
            var match = null;
            ValueTypes.ALL.forEach((valueType: ValueType) => {
                if (valueType.toString() == name) {
                    match = valueType;
                }
            });

            api.util.assertNotNull(match, "Uknown ValueType: " + name);
            return match;
        }
    }
}