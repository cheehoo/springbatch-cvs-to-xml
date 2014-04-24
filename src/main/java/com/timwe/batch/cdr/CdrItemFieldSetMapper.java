package com.timwe.batch.cdr;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * A @{link FieldSetMapper} implementation that binds the @{link FieldSet} into @{link CdrItem}
 * 
 * @author cheehoo
 * @since 1.0.0
 */
public class CdrItemFieldSetMapper implements FieldSetMapper<CdrItem> {

	/**
	 * Binding the @{link FieldSet} from itemWriter into @{link CdrItem}
	 * 
	 * @param fieldSet the field that contains the line
	 * @return cdrItem the result of the binding
	 */
	@Override
	public CdrItem mapFieldSet(FieldSet fieldSet) throws BindException {
		CdrItem item = new CdrItem();
		item.setVar0(fieldSet.readString(0));
		item.setVar1(fieldSet.readString(1));
		item.setVar2(fieldSet.readString(2));
		item.setVar3(fieldSet.readString(3));
		item.setVar4(fieldSet.readString(4));
		item.setVar5(fieldSet.readString(5));
		item.setVar6(fieldSet.readString(6));
		item.setVar7(fieldSet.readString(7));
		item.setVar8(fieldSet.readString(8));
		item.setVar9(fieldSet.readString(9));
		item.setVar10(fieldSet.readString(10));
		item.setVar11(fieldSet.readString(11));
		item.setVar12(fieldSet.readString(12));
		item.setVar13(fieldSet.readString(13));
		item.setVar14(fieldSet.readString(14));
		item.setVar15(fieldSet.readString(15));
		item.setVar16(fieldSet.readString(16));
		item.setVar17(fieldSet.readString(17));
		item.setVar18(fieldSet.readString(18));
		item.setVar19(fieldSet.readString(19));
		item.setVar20(fieldSet.readString(20));
		item.setVar21(fieldSet.readString(21));
		item.setVar22(fieldSet.readString(22));
		item.setVar23(fieldSet.readString(23));
		item.setVar24(fieldSet.readString(24));
		item.setVar25(fieldSet.readString(25));
		item.setVar26(fieldSet.readString(26));
		item.setVar27(fieldSet.readString(27));
		item.setVar28(fieldSet.readString(28));
		item.setVar29(fieldSet.readString(29));
		item.setVar30(fieldSet.readString(30));
		item.setVar31(fieldSet.readString(31));
		item.setVar32(fieldSet.readString(32));
		item.setVar33(fieldSet.readString(33));
		item.setVar34(fieldSet.readString(34));
		item.setVar35(fieldSet.readString(35));
		item.setVar36(fieldSet.readString(36));
		item.setVar37(fieldSet.readString(37));
		item.setVar38(fieldSet.readString(38));
		item.setVar39(fieldSet.readString(39));
		item.setVar40(fieldSet.readString(40));
		item.setVar41(fieldSet.readString(41));
		item.setVar42(fieldSet.readString(42));
		item.setVar43(fieldSet.readString(43));
		
		return item;
	}

}