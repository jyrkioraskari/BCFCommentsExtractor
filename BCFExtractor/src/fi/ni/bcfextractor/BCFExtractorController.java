package fi.ni.bcfextractor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;

import fi.ni.bcfextractor.vo.BCF;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

public class BCFExtractorController implements Initializable {
	@FXML
	MenuBar myMenuBar;

	@FXML
	MenuItem saveCSV;

	@FXML
	private TableView tableview;

	@FXML
	private TableColumn columnComment;

	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private void aboutAction() {
		// get a handle to the stage
		Stage stage = (Stage) myMenuBar.getScene().getWindow();
		new About(stage).show();
	}

	@FXML
	private void closeApplicationAction() {
		// get a handle to the stage
		Stage stage = (Stage) myMenuBar.getScene().getWindow();
		stage.close();
	}

	FileChooser fc;

	@FXML
	private void openBCFZIP() {
		Stage stage = (Stage) myMenuBar.getScene().getWindow();
		File file = null;

		if (fc == null) {
			fc = new FileChooser();
			fc.setInitialDirectory(new File("."));
		}
		FileChooser.ExtensionFilter ef1;
		ef1 = new FileChooser.ExtensionFilter("BCF ZIP package (*.bcfzip)", "*.bcfzip");
		FileChooser.ExtensionFilter ef2;
		ef2 = new FileChooser.ExtensionFilter("All Files", "*.*");
		fc.getExtensionFilters().clear();
		fc.getExtensionFilters().addAll(ef1, ef2);

		if (file == null)
			file = fc.showOpenDialog(stage);
		if (file == null)
			return;
		fc.setInitialDirectory(file.getParentFile());
		openBCFZip(file);
		saveCSV.setDisable(false);
	}

	private final ObservableList<BCF> data =  FXCollections.observableArrayList();

	private void openBCFZip(File file) {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			ZipInputStream zInputStream = new ZipInputStream(inputStream);
			try {
				data.clear();
				for (ZipEntry entry = zInputStream.getNextEntry(); entry != null; entry = zInputStream.getNextEntry()) {
					String name = entry.getName();
					 if (name.endsWith(".bcf")) {
							
							BufferedReader inputReader = new BufferedReader(new InputStreamReader(zInputStream));
					        StringBuilder sb = new StringBuilder();
					        String inline = "";
					        while ((inline = inputReader.readLine()) != null) {
					          sb.append(inline);
					        }

					        System.out.println(sb.toString());
					        SAXBuilder builder = new SAXBuilder();

					        try {
								Document doc = (Document) builder.build(new ByteArrayInputStream(sb.toString().getBytes()));
								listChildren(doc.getRootElement());      
							} catch (JDOMException e) {
								e.printStackTrace();
							} 
					 }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
			try {
				zInputStream.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	 private void listChildren(Element current) {
		   
		    System.out.println(current.getName());
		    if(current.getName().equals("Comment"))
		    {
		    	List<Content> contents = current.getContent();
		    	for(Content c:contents)
		    	if (c instanceof Text) {
		    		String txt=((Text) c).getTextNormalize();
		    		if(txt.length()>0)
		              data.add(new BCF("", txt));
		          }
		    }
		    List children = current.getChildren();
		    Iterator iterator = children.iterator();
		    while (iterator.hasNext()) {
		      Element child = (Element) iterator.next();
		      listChildren(child);
		    }
		    
		  }
		  
	@FXML
	private void saveCSV() {

	Stage stage = (Stage) myMenuBar.getScene().getWindow();
	File file = null;

	if (fc == null) {
		fc = new FileChooser();
		fc.setInitialDirectory(new File("."));
	}
	FileChooser.ExtensionFilter ef1;
	ef1 = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
	fc.getExtensionFilters().clear();
	fc.getExtensionFilters().addAll(ef1);

	if (file == null)
		file = fc.showSaveDialog(stage);
	if (file == null)
		return;
	fc.setInitialDirectory(file.getParentFile());
	saveCSV(file);
}

private void saveCSV(File file) {
	String fp = file.getAbsolutePath();
	PrintWriter writer = null;
	try {
		writer = new PrintWriter(fp, "UTF-8");
		for(BCF bcf:data)
		  writer.println(bcf.getComment());
		
	} catch (FileNotFoundException | UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	finally
	{
		writer.close();
	}
}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	
		tableview.setEditable(true);
		columnComment.setCellValueFactory(new PropertyValueFactory<BCF, String>("comment"));
		tableview.setItems(data);

	}
}
