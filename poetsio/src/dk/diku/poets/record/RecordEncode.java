package dk.diku.poets.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.diku.poets.gen.thrift.type.Typ;
import dk.diku.poets.gen.thrift.type.Type;
import dk.diku.poets.gen.thrift.type.TypeConstant;
import dk.diku.poets.gen.thrift.value.DateTime;
import dk.diku.poets.gen.thrift.value.RefVal;
import dk.diku.poets.gen.thrift.value.Val;
import dk.diku.poets.gen.thrift.value.Value;
import dk.diku.poets.record.Calendar;
/**
 * Encodes {@link PoetsValue} and {@link PoetsType} to thrift values and 
 * types.
 * 
 * @author jonsson,jespera
 *
 */
public class RecordEncode {
	public static Value encodeValue(PoetsValue recValue){
		Value value = new Value();
		value.values = new HashMap<Integer, Val>();
		value.root = encodeDispatch(value, 0, recValue);

		//System.out.println("[Encode] encoding record: " + recValue + " -> "+value);
		return value;
	}
	private static int encodeDispatch(Value value, int nextIndex, PoetsValue recValue){
		//System.out.println("[Encode] encoding record: " + recValue);

		//Dispatch over the type
		if(recValue.isRec()){
			PoetsValue.RecV r = (PoetsValue.RecV) recValue;
			if(r.getIsAbstract()){
				if(r.getInstance() != null){
					r = r.getInstance();
				}else{
					System.out.println("[Encode] *** Can not encode an abstract record");
					return 0;
				}
			}
			HashMap<String, Integer> fields = new HashMap<String, Integer>();

			for(String key : r.val.keySet()){
				nextIndex = encodeDispatch(value, nextIndex, r.val.get(key));
				fields.put(key, nextIndex);
				nextIndex++;
			}
			Val tRecVal = new Val();
			dk.diku.poets.gen.thrift.value.Record rec = new dk.diku.poets.gen.thrift.value.Record();
			rec.recordName = r.getName();
			rec.fields = fields;
			tRecVal.recordVal = rec;
			value.values.put(nextIndex, tRecVal);

		}else if(recValue.isInt()){
			PoetsValue.IntV r = (PoetsValue.IntV) recValue;
			Val sVal = new Val();
			sVal.setIntVal(r.val);
			value.values.put(nextIndex, sVal);
		}else if(recValue.isString()){
			PoetsValue.StringV r = (PoetsValue.StringV) recValue;
			Val sVal = new Val();
			sVal.setStringVal(r.val);
			value.values.put(nextIndex, sVal);
		}else if(recValue.isDouble()){
			PoetsValue.DoubleV r = (PoetsValue.DoubleV) recValue;
			Val sVal = new Val();
			sVal.setDoubleVal(r.val);
			value.values.put(nextIndex, sVal);
		}else if(recValue.isDateTime()){
			PoetsValue.DateTimeV r = (PoetsValue.DateTimeV) recValue;
			Val sVal = new Val();
			sVal.setDateTimeVal(encodeCalendarToDateTime(r.val));
			value.values.put(nextIndex, sVal);
		}else if(recValue.isBool()){
			PoetsValue.BoolV r = (PoetsValue.BoolV) recValue;
			Val sVal = new Val();
			sVal.setBoolVal(r.val);
			value.values.put(nextIndex, sVal);
		}else if(recValue.isRef()){
			PoetsValue.RefV r = (PoetsValue.RefV) recValue;
			Val rVal = new Val();
			RefVal refVal = new RefVal();
			refVal.recordName = r.getRefName();
			if(r.getRefPointer() == -1){
				System.err.println("[RecordEncode] *** Pointer "+r.getRefName()+ 
						" is not set ");
			}else{
				refVal.refPointer = r.getRefPointer();
				rVal.setRefVal(refVal);
				value.values.put(nextIndex, rVal);
			}
		}else if(recValue.isList()){
			PoetsValue.ListV r = (PoetsValue.ListV) recValue;

			Val listVal = new Val();
			List<Integer> idxList = new ArrayList<Integer>();
			for(PoetsValue val : r.val){
				nextIndex = encodeDispatch(value, nextIndex, val);
				idxList.add(nextIndex);
				nextIndex++;
			}
			listVal.listVals = idxList;
			value.values.put(nextIndex, listVal);
		}else{
			System.err.println("[Encode] *** Unrecognized PoetsValue ");
			return 0;
		}		

		return nextIndex;
	}
	public static DateTime encodeCalendarToDateTime(Calendar cal) {
		DateTime dt = new DateTime();
		dt.year = cal.get(Calendar.YEAR);
		dt.month = cal.get(Calendar.MONTH);
		dt.day = cal.get(Calendar.DAY_OF_MONTH);
		dt.hour = cal.get(Calendar.HOUR);
		dt.minutes = cal.get(Calendar.MINUTE);
		dt.seconds = cal.get(Calendar.SECOND);
		dt.miliseconds = 0;
		return dt;
	}
	public static Type encodeType(String recordName){
		Type t = new Type();
		t.types = new HashMap<Integer, Typ>(1);
		t.root = 0;
		Typ typ = new Typ();
		typ.setTypeRecord(recordName);
		t.types.put(0, typ);
		return t;
	}
	
	public static Type encodeType(PoetsValue value){
		Type t = new Type();
		t.types = new HashMap<Integer, Typ>(1);
		t.root = encodeTypeDispatch(t, 0, value);
		return t;
	}
	private static int encodeTypeDispatch(Type type, int nextIndex, PoetsValue value){		
		if(value.isBool()){
			Typ typ = new Typ();
			typ.setTypeConstant(TypeConstant.Bool);
			type.types.put(nextIndex, typ);
		}else if(value.isDateTime()){
			Typ typ = new Typ();
			typ.setTypeConstant(TypeConstant.DateTime);
			type.types.put(nextIndex, typ);
		}else if(value.isDouble()){
			Typ typ = new Typ();
			typ.setTypeConstant(TypeConstant.Double);
			type.types.put(nextIndex, typ);			
		}else if(value.isInt()){
			Typ typ = new Typ();
			typ.setTypeConstant(TypeConstant.Int);
			type.types.put(nextIndex, typ);
		}else if(value.isString()){
			Typ typ = new Typ();
			typ.setTypeConstant(TypeConstant.String);
			type.types.put(nextIndex, typ);			
		}else if(value.isList()){
			Typ typ = new Typ();
			PoetsValue.ListV lValue = (PoetsValue.ListV) value;
			typ.setTypeList(encodeTypeDispatch(type, nextIndex+1, lValue.getElementType()));
			type.types.put(nextIndex, typ);
			nextIndex++;
		}else if(value.isRec()){
			Typ typ = new Typ();
			PoetsValue.RecV recValue = (PoetsValue.RecV) value;
			typ.setTypeRecord(recValue.getName());
			type.types.put(nextIndex, typ);
			for(PoetsValue pv : recValue.val.values()){
				// Do we really need to do this?
			}
		}else if(value.isRef()){
			Typ typ = new Typ();
			PoetsValue.RefV refValue = (PoetsValue.RefV) value;
			typ.setTypeRef(refValue.getRefName());
			type.types.put(nextIndex, typ);
		}
		return nextIndex;
	}
}
