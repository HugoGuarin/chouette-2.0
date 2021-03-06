package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class ChouetteAreaEnumConverter extends StrutsTypeConverter
{
	@SuppressWarnings("rawtypes")
   @Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().equals("")) {
            return null;
        }
		if (ChouetteAreaEnum.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return ChouetteAreaEnum.fromValue(value[0]);
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(ChouetteAreaEnum.class))
		{
			throw new TypeConversionException();
		}
		return ((ChouetteAreaEnum)arg1).toString();
	}
}