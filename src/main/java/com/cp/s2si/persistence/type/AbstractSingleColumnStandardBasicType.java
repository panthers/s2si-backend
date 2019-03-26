/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.cp.s2si.persistence.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * Copied from class org.hibernate.type.AbstractSingleColumnStandardBasicType
 *
 * @author Steve Ebersole
 * @author panther Copied but removed final for Dialect exposer
 */
public abstract class AbstractSingleColumnStandardBasicType<T>
		extends AbstractStandardBasicType<T>
		implements SingleColumnType<T> {

	private static final long serialVersionUID = 7806404988249768411L;

	public AbstractSingleColumnStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, SqlTypeDescriptor fallbackSqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
		super( sqlTypeDescriptor, fallbackSqlTypeDescriptor, javaTypeDescriptor );
	}

	@Override
	public final int sqlType() {
		return getSqlTypeDescriptor().getSqlType();
	}

	@Override
	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if ( settable[0] ) {
			nullSafeSet( st, value, index, session );
		}
	}
}
