package com.sunlights.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuan on 9/22/14.
 */
public class Message {

    private static final String SEVERITY_INFO_NAME = "INFO";
    public static final Severity SEVERITY_INFO = new Severity(SEVERITY_INFO_NAME);

    private static final String SEVERITY_WARN_NAME = "WARN";
    public static final Severity SEVERITY_WARN = new Severity(SEVERITY_WARN_NAME);

    private static final String SEVERITY_ERROR_NAME = "ERROR";
    public static final Severity SEVERITY_ERROR = new Severity(SEVERITY_ERROR_NAME);

    private static final String SEVERITY_FATAL_NAME = "FATAL";
    public static final Severity SEVERITY_FATAL = new Severity(SEVERITY_FATAL_NAME);

    private Severity severity = Message.SEVERITY_INFO;
    private String code = null;
    private String summary = null;
    private String detail = null;
    private Map<String, String> fields = new HashMap<String, String>();

    public Message() {
        super();
    }
    public Message(String code ,String summary) {
        this.code = code;
        this.summary = summary;
    }

    public Message(String code ,String summary, String detail) {
        this.code = code;
        this.summary = summary;
        this.detail = detail;
    }

    public Message(Severity severity){
        this.severity = severity;
    }

    public Message(Severity severity, String summary, String detail) {
        this.severity = severity;
        this.summary = summary;
        this.detail = detail;
    }

    public Message(Severity severity, String code ,String summary, String detail) {
        this.severity = severity;
        this.code = code;
        this.summary = summary;
        this.detail = detail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetail() {
        if (this.detail == null) {
            return (this.summary);
        } else {
            return (this.detail);
        }
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }


    public Map<String, String> getFields() {
		return fields;
	}
	public void put(String key, String value) {
        fields.put(key, value);
    }

    public void remove(String key) {
        fields.remove(key);
    }

    public void clear() {
        fields.clear();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static class Severity implements Comparable {

        private Severity(String newSeverityName) {
            severityName = newSeverityName;
        }

        private final int ordinal = nextOrdinal++;

        String severityName = null;

        public int compareTo(Object other) {
            return this.ordinal - ((Severity) other).ordinal;
        }

        public int getOrdinal() {
            return (this.ordinal);
        }

        public String toString() {
            if (null == severityName) {
                return (String.valueOf(this.ordinal));
            }
            return (String.valueOf(this.severityName) + ' ' + this.ordinal);
        }

        private static int nextOrdinal = 0;

    }
}
