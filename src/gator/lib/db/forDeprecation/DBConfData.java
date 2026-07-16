/*
 * Copyright (C) 2023 Sergio Basurto Juárez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gator.lib.db.forDeprecation;

import gator.lib.db.GappDBConnection;

/**
 * DBData class is the class used to store database's connection information
 * and all its attributes, inherits from DBConf.
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 *
 * @deprecated 	This is deprecated in favor of GappDBConnection that use json configuration instead.
 * @see GappDBConnection
 */
@Deprecated
public class DBConfData extends DBConf {
	/**
	 * Allow to set connection's info.
	 * @param conn A GappDBConnection instance with connection information.
	 */
	public synchronized void setConf(GappDBConnection conn) {
		try{
			confLock.lock();
			ipv4 = conn.getServerIP("v4");
			ipv6 = conn.getServerIP("v6");
			puerto = conn.getPort();
			sid = conn.getSID();
			usr = conn.getUser();
			passwd = conn.getPasswd();
			serverName = conn.getServerName();
			dbName = conn.getDBName();
			serverID = conn.getServerID();
			isSetConf = true;
			dbKind = conn.getDBKind();
		}finally{
			confLock.unlock();
		}
	}
}
