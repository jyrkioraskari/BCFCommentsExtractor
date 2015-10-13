package fi.ni.bcfextractor.vo;

/*
 * The GNU Affero General Public License
 * 
 * Copyright (c) 2015 Jyrki Oraskari (Jyrki.Oraskari@aalto.fi / jyrki.oraskari@aalto.fi)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BCF {
	private final StringProperty file_name;
	private final StringProperty comment;

	public BCF(String file_name, String comment) {
		super();
		this.file_name =  new SimpleStringProperty(file_name);
		this.comment =  new SimpleStringProperty(comment);
	}

	public String getFile_name() {
		return file_name.get();
	}

	public String getComment() {
		return comment.get();
	}

}
