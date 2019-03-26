/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.cp.s2si.persistence.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

/**
 * A type that maps between {@link java.sql.Types#NVARCHAR NVARCHAR} and {@link String}
 * Copied from class org.hibernate.type.StringNVarcharType
 * Will set {@link java.sql.Types#VARCHAR VARCHAR} for mysql databases. 
 * All others databases would be mapped to nvarchar
 *
 * @author Gavin King
 * @author Steve Ebersole
 * @author panther
 */
public class CustomStringNVarcharType
		extends AbstractSingleColumnStandardBasicType<String>
		implements DiscriminatorType<String> {

	private static final long serialVersionUID = -1909235761793179255L;
	public static final CustomStringNVarcharType INSTANCE = new CustomStringNVarcharType();

	public CustomStringNVarcharType() {
		super( NVarcharTypeDescriptor.INSTANCE, VarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE );
	}

	public String getName() {
		return "custom_nstring";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return false;
	}

	public String objectToSQLString(String value, Dialect dialect) throws Exception {
		return '\'' + value + '\'';
	}

	public String stringToObject(String xml) throws Exception {
		return xml;
	}

	public String toString(String value) {
		return value;
	}
}
