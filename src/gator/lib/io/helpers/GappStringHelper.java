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
package gator.lib.io.helpers;

/**
 * This class provides the utils for manage strings, for example normalize and
 * unormalize strings accordingly to Soft Gator standards.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappStringHelper {
        /**
         * Normalize a string accordingly to Soft Gator application normalization.
         * @param toNormalize String to be normalized.
         * @return The normalized string.
         */
	public String normalizeStr(String toNormalize) {
		String normalizedStr = toNormalize;
		normalizedStr = normalizedStr.replaceAll("&", "::amp::");
		normalizedStr = normalizedStr.replaceAll(",", "::comma::");
		normalizedStr = normalizedStr.replaceAll("\\$", "::dollar::");
		normalizedStr = normalizedStr.replaceAll("/", "::slash::");
		normalizedStr = normalizedStr.replaceAll("\\\\", "::backslash::");
		normalizedStr = normalizedStr.replaceAll("\\(", "::lparenthesis::");
		normalizedStr = normalizedStr.replaceAll("\\)", "::rparenthesis::");
		normalizedStr = normalizedStr.replaceAll("\"", "::doublequote::");
		normalizedStr = normalizedStr.replaceAll("¿", "::lquestionmark::");
		normalizedStr = normalizedStr.replaceAll("\\?", "::rquestionmark::");
		normalizedStr = normalizedStr.replaceAll("'", "''");
		normalizedStr = normalizedStr.replaceAll("#", "::numbersign::");
		normalizedStr = normalizedStr.replaceAll("%", "::percent::");
                normalizedStr = normalizedStr.replaceAll("á", "::aacute::");
                normalizedStr = normalizedStr.replaceAll("é", "::eacute::");
                normalizedStr = normalizedStr.replaceAll("í", "::íacute::");
                normalizedStr = normalizedStr.replaceAll("ó", "::oacute::");
                normalizedStr = normalizedStr.replaceAll("ú", "::uacute::");
                normalizedStr = normalizedStr.replaceAll("ñ", "::ntilde::");
                normalizedStr = normalizedStr.replaceAll("Á", "::Aacute::");
                normalizedStr = normalizedStr.replaceAll("É", "::Eacute::");
                normalizedStr = normalizedStr.replaceAll("Í", "::Iacute::");
                normalizedStr = normalizedStr.replaceAll("Ó", "::Oacute::");
                normalizedStr = normalizedStr.replaceAll("Ú", "::Uacute::");
                normalizedStr = normalizedStr.replaceAll("Ñ", "::Ntilde::");
		return normalizedStr;
	}
        /**
         * Tailor a string normalized accordingly to Soft Gator application normalization.
         * @param toUnormalize The string to tailored.
         * @return Clean string.
         */
	public String unormalizeStr(String toUnormalize) {
		String unormalizedStr = toUnormalize;
		unormalizedStr = unormalizedStr.replaceAll("::comma::",",");
		unormalizedStr = unormalizedStr.replaceAll("--comma--",",");
		unormalizedStr = unormalizedStr.replaceAll("::amp::", "&");
		unormalizedStr = unormalizedStr.replaceAll("::dolar::", "$");
		unormalizedStr = unormalizedStr.replaceAll("::slash::", "/");
		unormalizedStr = unormalizedStr.replaceAll("::backslash::", "\\");
		unormalizedStr = unormalizedStr.replaceAll("::quote::", "''");
		unormalizedStr = unormalizedStr.replaceAll("::quote2::", "''''");
		unormalizedStr = unormalizedStr.replaceAll("::doublequotes::", "\"");
		unormalizedStr = unormalizedStr.replaceAll("::lparenthesis::", "(");
		unormalizedStr = unormalizedStr.replaceAll("::rparenthesis::", ")");
		unormalizedStr = unormalizedStr.replaceAll("::lquestionmark::", "¿");
		unormalizedStr = unormalizedStr.replaceAll("::rquestionmark::", "?");
		unormalizedStr = unormalizedStr.replaceAll("::asterisk::", "*");
		unormalizedStr = unormalizedStr.replaceAll("::grave::", "`");
		unormalizedStr = unormalizedStr.replaceAll("::tilde::", "~");
		unormalizedStr = unormalizedStr.replaceAll("::equal::", "=");
		unormalizedStr = unormalizedStr.replaceAll(":-comma-:","::comma::");
		unormalizedStr = unormalizedStr.replaceAll("::numbersign::", "#");
		unormalizedStr = unormalizedStr.replaceAll("::percent::", "%");
                unormalizedStr = unormalizedStr.replaceAll("::aacute::", "á");
                unormalizedStr = unormalizedStr.replaceAll("::eacute::", "é");
                unormalizedStr = unormalizedStr.replaceAll("::iacute::", "í");
                unormalizedStr = unormalizedStr.replaceAll("::oacute::", "ó");
                unormalizedStr = unormalizedStr.replaceAll("::uacute::", "ú");
                unormalizedStr = unormalizedStr.replaceAll("::ntilde::", "ñ");
                unormalizedStr = unormalizedStr.replaceAll("::Aacute::", "Á");
                unormalizedStr = unormalizedStr.replaceAll("::Eacute::", "É");
                unormalizedStr = unormalizedStr.replaceAll("::Iacute::", "Í");
                unormalizedStr = unormalizedStr.replaceAll("::Oacute::", "Ó");
                unormalizedStr = unormalizedStr.replaceAll("::Uacute::", "Ú");
                unormalizedStr = unormalizedStr.replaceAll("::Ntilde::", "Ñ");
		return unormalizedStr;
	}
}
