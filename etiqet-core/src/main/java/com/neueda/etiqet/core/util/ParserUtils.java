package com.neueda.etiqet.core.util;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Field;
import com.neueda.etiqet.core.config.dtos.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ParserUtils {

	private static final Logger LOG = LogManager.getLogger(ParserUtils.class);

	private ParserUtils(){}

	/**
	 * Method to create a cdrItem named as key from a cdr parent
	 * @param key name of the cdrItem.
	 * @param rest child tags.
	 * @param value value
	 * @param parent parent.
	 * @return CdrItem
	 */
	private static CdrItem getCdrItem(String key, String rest, String value, Cdr parent) {
		CdrItem item = null;
		if (StringUtils.isNullOrEmpty(rest)) {
			item = new CdrItem();
			item.setStrval(value);
		} else {
			item = parent.getItem(key);
			String[] tag = rest.split(Separators.LEVEL_SEPARATOR);
			String restTags = getRestTags(tag, rest);

			Cdr cdrChild = null;
			if (item != null) {
				List<Cdr> children = item.getCdrs().stream().filter(x -> x.containsKey(tag[0])).collect(Collectors.toList());
				if (children != null && !children.isEmpty()) {
					cdrChild = children.get(0);
					CdrItem cdrItem = getCdrItem(tag[0], restTags, value, cdrChild);
					correctCdrItemType(cdrItem);
					cdrChild.setItem(tag[0], cdrItem);
				} else {
					cdrChild = new Cdr("List");
					CdrItem cdrItem = getCdrItem(tag[0], restTags, value, cdrChild);
					correctCdrItemType(cdrItem);
					cdrChild.setItem(tag[0], cdrItem);
					if (item.getCdrs() == null) {
						 item.setCdrs(new ArrayList<>());
					}
					item.getCdrs().add(cdrChild);
				}
			} else {
				item = new CdrItem();				
				cdrChild = new Cdr("List");
				CdrItem cdrItem = getCdrItem(tag[0], restTags, value, cdrChild);
				correctCdrItemType(cdrItem);
				cdrChild.setItem(tag[0], cdrItem);
				item.setCdrs(new ArrayList<>());
				item.getCdrs().add(cdrChild);
			}
		}		
		
		return item;
	}

	/**
	 * Simple method to return rest tags if available else empty string
	 * @param tag array of tags
	 * @param rest string representation of the tags
	 * @return rest of the tree, or empty string if not found
	 */
	public static String getRestTags(String[] tag, String rest){
	    if(tag.length > 1) {
	        return rest.replaceFirst(tag[0] + Separators.LEVEL_SEPARATOR, "");
        } else {
	        return "";
        }
	}

	/**
	 * By default, when creating a CdrItem, the type is set to CDR_STRING and never changed in {@link #getCdrItem}
	 * This means every non-String CdrItem is handled incorrectly.
	 * @param cdrItem CdrItem that needs its type corrected
	 */
	private static void correctCdrItemType(CdrItem cdrItem) {
		if(cdrItem.getIntval() != null) {
			cdrItem.setType(CdrItem.CdrItemType.CDR_INTEGER);
		} else if(cdrItem.getDoubleval() != null) {
			cdrItem.setType(CdrItem.CdrItemType.CDR_DOUBLE);
		} else if(cdrItem.getCdrs() != null) {
			cdrItem.setType(CdrItem.CdrItemType.CDR_ARRAY);
		} else if(cdrItem.getBoolVal() != null) {
			cdrItem.setType(CdrItem.CdrItemType.CDR_BOOLEAN);
		} else {
			cdrItem.setType(CdrItem.CdrItemType.CDR_STRING);
		}
	}

	/**
	 * Method to convert a string witch pattern attr1.attr12=value,attr2=value...
     * @param mType message type
	 * @param params String containing a list of comma separated key values.
	 * @return CDR message containing the parsed parameters.
	 */
	public static Cdr stringToCdr(String mType, String params) {
		Cdr cdr = new Cdr(mType);
		if (!StringUtils.isNullOrEmpty(params)) {
			String[] param = params.trim().split(Separators.PARAM_SEPARATOR);
			for (String index: param) {
				String[] keyValue = index.split(Separators.KEY_VALUE_SEPARATOR);
				// Skip parameters without value
				if(keyValue.length != 2) {
					LOG.warn("Skipping parameter [" + index + "]. No value or separator found");
					continue;
				}
				String key = keyValue[0].trim();
				String value = keyValue[1];
				String[] tag = key.split(Separators.LEVEL_SEPARATOR);
				String restTags = null;
				if (tag.length > 1) {
					restTags = key.replaceFirst(tag[0] + Separators.LEVEL_SEPARATOR, "");
				}
				CdrItem item = getCdrItem(tag[0], restTags, value, cdr);
				correctCdrItemType(item);
				cdr.setItem(tag[0], item);
			}
		}
		return cdr;
	}
	
	/**
	 * Method to check if a tag is inside the cdr structure.
	 * @param tag tag to find.
	 * @param cdr structure where to find.
	 * @return result of check.
	 */
	public static boolean isTagInCdr(String tag, Cdr cdr) {
		if (cdr.containsKey(tag)) {
			return true;
		} else if (!ArrayUtils.isNullOrEmpty(cdr.getItems())) {
			return cdr.getItems().entrySet().stream()
                        // filter out non-array children
                        .filter(entry -> !ArrayUtils.isNullOrEmpty(entry.getValue().getCdrs()))
                        // get all the children as a list of CDRs using flatMap
                        .map(entry -> entry.getValue().getCdrs())
                        .flatMap(List::stream)
                        // check whether any of the children match, calling this recursively
                        .anyMatch(msg -> isTagInCdr(tag, msg));
        } else {
		    return false;
        }
	}
	
	/**
	 * Method to create the value if a field only if it is a leaf.
	 * @param tag tag to find.
	 * @param cdr structure where to find.
	 * @return result of check.
	 */
	public static Object getTagValueFromCdr(String tag, Cdr cdr) {
		Object tagValue = null;
		if (cdr.containsKey(tag)) {
			tagValue = cdr.getAsString(tag);
		} else {
			Map<String, CdrItem> cdrItems = cdr.getItems(); 
			if (cdrItems != null && !cdrItems.isEmpty()) {
				tagValue = searchBranches(cdrItems, tag, cdr);
			}
		}
		return tagValue;
	}

	/**
	 * Iterate through the various leafs of the cdr checking for tag returning it if found
	 * @param cdrItems
	 * @param tag
	 * @param cdr
	 * @return
	 */
	private static Object searchBranches(Map<String, CdrItem> cdrItems, String tag, Cdr cdr){
		Iterator<String> ite = cdrItems.keySet().iterator();
		Object tempFound = null;
		while (tempFound == null && ite.hasNext()) {
			CdrItem cdrItem = cdr.getItem(ite.next());
			if (cdrItem.getCdrs() != null && !cdrItem.getCdrs().isEmpty()) {
				int index = 0;
				while (tempFound == null && index < cdrItem.getCdrs().size()) {
					tempFound = getTagValueFromCdr(tag, cdrItem.getCdrs().get(index++));
				}
			}
		}
		return tempFound;
	}
	
	/**
	 * Method to create the value if a field only if it is a leaf.
	 * @param tag tag to find.
	 * @param cdr structure where to find.
	 * @return result of check.
	 */
	public static Object getFullTagValueFromCdr(String tag, Cdr cdr) {
		Object tagValue = null;
		
		if (!tag.contains(Separators.LEVEL_SEPARATOR)) {
			tagValue = cdr.getAsString(tag);
		} else {
			String actualTag = tag.trim().split(Separators.LEVEL_SEPARATOR)[0];
			String restTags = tag.replaceFirst(actualTag + Separators.LEVEL_SEPARATOR, "");
			CdrItem item = cdr.getItem(actualTag);
			if (item != null) {
				Object tempFound = null;
				Iterator<Cdr> ite = item.getCdrs().iterator();
				while (tempFound == null && ite.hasNext()) {
					Cdr cdrItem = ite.next();
					tempFound = getFullTagValueFromCdr(restTags, cdrItem);
				}
				if (tempFound != null) {
					tagValue = tempFound;
				}
			}
		}
		return tagValue;
	}

	/**
	 * This method is used to build a message generating default values for required params
	 * @param message message type defaults from the ProtocolConfig
	 * @param cdr message to populate with default values
	 * @throws EtiqetException when a utility method cannot be used to populate the field
	 */
	public static void fillDefault(Message message, Cdr cdr) throws EtiqetException {
		if (message != null && message.getFields() != null && message.getFields().getField() != null) {
			for(Field field : message.getFields().getField()) {
				if(!cdr.containsKey(field.getName())) {
				    handleDefaultField(field, cdr);
                }
			}
		}
	}

	/**
	 * This method is used to check a field matches a given date/datetime format logging a warning were appropriate
	 * @param error Error message if format isn't matched
	 * @param fieldName Field name, used for logging
	 * @param fieldType Field type, used for logging
	 * @param value value of the field
	 * @param format timestamp format to try parse
	 */
	public static Boolean matchDateTimeFormat(String error, String fieldName, String fieldType, String value, String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.parse(value);
			return true;
		} catch (ParseException e) {
			LOG.warn(String.format(error, fieldName, fieldType, value));
			return false;
		}
	}

	/**
	 * Gets the field type from ProcotolConfig
	 * @param field field to be configured
	 * @return String representing field type
	 */
	public static String getFieldType(Field field){
		String fieldType = field.getType() == null ? "string" : field.getType();
		if(field.getName().toLowerCase().contains("time")){
			fieldType = "datetime";
		} else if(field.getName().toLowerCase().contains("date")) {
			fieldType = "date";
		}
		return fieldType;
	}

	/**
	 * Converts boolean-ish string values to a Boolean type (e.g. Y, false, 0, 1)
	 * @param value boolean value as String
	 * @return Boolean value of the string
	 * @throws EtiqetException
	 */
	public static Boolean getBoolean(String value) throws EtiqetException {
		Boolean returnBool;
		if(Arrays.asList("false", "n", "0").contains(value.toLowerCase())) {
			returnBool=Boolean.FALSE;
		} else if(Arrays.asList("true", "y", "1").contains(value.toLowerCase())) {
			returnBool = Boolean.TRUE;
		} else {
		    throw new EtiqetException("Unable to convert bool");
        }
		return returnBool;
	}

	/**
	 * Populates the CDR with default field values
	 * @param field Field from the protocol
	 * @param cdr message to populate with the field, if not already set
	 */
	private static void handleDefaultField(Field field, Cdr cdr) throws EtiqetException {
	    Object value;
        if(field.hasStaticMethod()) {
            try {
                Class<?> utilClass = Class.forName(field.getUtilclass());
                Method method = utilClass.getDeclaredMethod(field.getMethod());
                value = method.invoke(utilClass.newInstance());
            } catch (Exception e) {
                // need to throw this as a runtime exception because the function is called from a stream
                throw new EtiqetException("Could not get Util Class for field " + field.toString(), e);
            }
        } else {
            value = field.getValue();
        }
        String fieldName = field.getName();
        String fieldType = getFieldType(field);

        final String ERROR_FORMAT = "Unable to parse %s as type %s (value: %s), defaulting to String type";
        switch (fieldType) {
            case "integer":
                try {
                    cdr.set(fieldName, Integer.parseInt(value.toString()));
                } catch (NumberFormatException lnf) {
                    LOG.warn(String.format(ERROR_FORMAT, fieldName, "Integer", value.toString()));
                    cdr.set(fieldName, value.toString());
                }
                break;
            case "double":
                try {
                    cdr.set(fieldName, Double.parseDouble(value.toString()));
                } catch (NumberFormatException lnf) {
                    LOG.warn(String.format(ERROR_FORMAT, fieldName, "Double", value.toString()));
                    cdr.set(fieldName, value.toString());
                }
                break;
            case "boolean":
                try{
					cdr.set(fieldName, getBoolean(value.toString()));
				}catch (EtiqetException e){
                    LOG.warn(String.format(ERROR_FORMAT, fieldName, "Boolean", value.toString()));
                    cdr.set(fieldName, value.toString());
                }
                break;
			case "date":
				matchDateTimeFormat(ERROR_FORMAT, fieldName, fieldType, value.toString(), "yyyyMMdd");
				cdr.set(fieldName, value.toString());
				break;
			case "datetime":
				matchDateTimeFormat(ERROR_FORMAT, fieldName, fieldType, value.toString(), "yyyyMMdd-HH:mm:ss.SSSSSSSSS");
				cdr.set(fieldName, value.toString());
				break;
            case "string":
            default:
                cdr.set(fieldName, value.toString());
        }
    }
}
