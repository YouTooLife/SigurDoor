package net.youtoolife.sigur;


import java.io.File;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.youtoolife.sigur.view.MainController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	public Stage primaryStage;
	
	public Thread dbHandler = null;
	public Thread sender = null;

	public String dir = System.getProperty("user.home")+"/YouTooLife/SigurDoor/";
	public String dir2 = "";
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			String myJarPath = System.getProperty("java.class.path");
			System.out.println("paths: "+myJarPath);
			String paths[] = myJarPath.split(":");
			if (paths.length > 0) {	
				for (String s:paths)
					System.out.println(s);
			}
			dir2 = new File(paths[0]).getParent();
			System.out.println(dir2);
			//System.out.println(getClass().getResource("view/Main.fxml").toExternalForm());
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("view/Main.fxml"));
			Scene scene = new Scene(root, 800, 500);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("view/MainForm.fxml"));
	        AnchorPane overview = (AnchorPane) loader.load();
	        root.setCenter(overview);
	        
	        MainController controller = loader.getController();
	        this.primaryStage = primaryStage;
	        primaryStage.setTitle("PUSH Akhmarov Isa");
	        primaryStage.setResizable(false);
	        
	        
	        
	        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					System.out.println("closing...");
					try {
						controller.saveSettings();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
	        controller.setMainApp(this);
	        //System.out.println("BG start");
	        //controller.setBG();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		
		launch(args);
	}
}
