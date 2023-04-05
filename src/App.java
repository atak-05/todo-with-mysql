import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
// import javax.swing.JTextField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

public class App {
    static ArrayList<Object> todoList = new ArrayList<Object>();
    public static void main(String[] args) throws Exception {
        
        start();
    }
    private static void start() throws IOException, ClassNotFoundException, SQLException {

        
        String str = "Lütfen Seçim Yapınız";
        //devam edilecektir...
        str+="\n****************\n";
        str+="\n [1] Listele ";
        str+="\n [2] Ekle ";
        str+="\n [3] Sil ";
        str+="\n [4] Update ";
        str+="\n [0 veya Cancel] Çıkış ";

        String choice = JOptionPane.showInputDialog(null, str);

        if(choice == null){
            choice= "0";
        }else if (choice.equals("")||choice.matches("[^0-9]+")){
            start();
        }
        switch (choice) {
            case "0":
                exit();
                break;
            case "1":
                list();
                break;
            case "2":
                addTodo();
                break;
            case "3":
                delTodo();
                break;
            case "4":
                UpTodo();
                break;
            default:
                break;
        }

    }
    //*   For Connection to Mysql */

    private static void UpTodo() throws ClassNotFoundException, SQLException, IOException {
        Connection connection = getConnect();
        int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Güncellenecek Id"));
        int isCompleted = 0;
    
        // Fetch current isCompleted value for the given id
        String sql1 = "SELECT * FROM todos WHERE id = ?";
        PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
        preparedStatement1.setInt(1, id);
        ResultSet resultSet = preparedStatement1.executeQuery();
        if (resultSet.next()) {
            isCompleted = resultSet.getInt("isCompleted");
        } else {
            JOptionPane.showMessageDialog(null, "Belirtilen id ile bir kayıt bulunamadı!");
            list();
        }
    
        // Update isCompleted value to the opposite value
        isCompleted = (isCompleted == 1) ? 0 : 1;
        String sql2 = "UPDATE todos SET isCompleted = ? WHERE id = ?";
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
        preparedStatement2.setInt(1, isCompleted);
        preparedStatement2.setInt(2, id);
        int rowsAffected = preparedStatement2.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Kayıt başarıyla güncellendi.");
        } else {
            JOptionPane.showMessageDialog(null, "Kayıt güncellenirken bir hata oluştu!");
        }
    
        list();
    }
    
    private static Connection getConnect() throws ClassNotFoundException, SQLException {
        Conn conn = new Conn();
        Connection  connect = null;
        Class.forName(conn.getJdbcDriver());
        System.out.println("Connected to Database");
        connect = DriverManager.getConnection(conn.getUrl(),conn.getUser(),conn.getPassword());
        System.out.println("Opened to Database");
        return connect;
    }

    //* ***************************** delTodo **************************************** */

    private static void delTodo() throws IOException, ClassNotFoundException, SQLException {

        int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Silinecek Id!"));
        try (Connection connection = getConnect();
        PreparedStatement statement = connection.prepareStatement(
             "DELETE FROM todos WHERE id =(?)")) {
    
            statement.setInt(1, id);
            
            int rowsDeleted = statement.executeUpdate();
            System.out.println(rowsDeleted + " rows inserted.");  
            JOptionPane.showMessageDialog(null,rowsDeleted+" kayıt silindi..");
        
             }       
        list();
        }

    //* ****************************** addTodo ************************************** */

    private static void addTodo() throws IOException, ClassNotFoundException, SQLException {
        JTextField title = new JTextField(5);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Title:"));
        panel.add(title);
        int confirm = JOptionPane.showConfirmDialog(null, panel, "Lütfen başlık biligisini yazınız!", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {

            String dataTitle = title.getText();
            try (Connection connection = getConnect();
                PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO todos(title, isCompleted) VALUES (?, ?)")) {
            
                statement.setString(1, dataTitle);
                statement.setInt(2, 0);
                
                int rowsInserted = statement.executeUpdate();
                System.out.println(rowsInserted + " rows inserted.");  
                JOptionPane.showMessageDialog(null,rowsInserted+" kayıt eklendi..");
        } 
        }else {
            addTodo();
            
        }
        start();
    }
    
    //* ****************************** list *************************************** */
    private static void list() throws IOException, ClassNotFoundException, SQLException {
    
        Object[] cols = {"id","title","isCompleted"};
        int rowCount = getRecordCount();
        Object[][] rows = new Object[rowCount][3];
        
        try (Connection connection = getConnect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM todos")) {
            int index = 0;
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                int isCompleted = resultSet.getInt("isCompleted");
                System.out.printf("%d, %s, %d\n", id, title, isCompleted);
                rows[index][0] =id;
                rows[index][1] =title;
                rows[index][2] =isCompleted;
                index++;
               
            }
        } 
        JTable table = new JTable(rows, cols);
        table.getColumnModel().getColumn(2).setCellRenderer(new CompletedCellRenderer());
        JOptionPane.showMessageDialog(null,new JScrollPane(table));

        start();

    }
  
    

    //* **************************** message ***************************************** */

   
    private static int getRecordCount() throws IOException, ClassNotFoundException, SQLException {
        int count = 0;
        try (Connection connection = getConnect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM todos")) {
        
            while (resultSet.next()) {
                count++;
            }
            return count;
        } 
        
    }
    // private static void message(String str) throws IOException, ClassNotFoundException, SQLException {
    //     JOptionPane.showMessageDialog(null, str);
    //     start();
    // }
    //* ****************************** exit ***************************************** */

    private static void exit() throws IOException, ClassNotFoundException, SQLException {
        int confirm = JOptionPane.showConfirmDialog(null, "Çıkmak istediğinizden emin misiniz?");
        if (confirm == 0){
            System.exit(0);

        }else{
            start();
        } 
    }

}
  class CompletedCellRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            if (value instanceof Integer) {
                int intValue = (Integer) value;
                if (intValue == 0) {
                    setText("Tamamlanmadı!");
                } else {
                    setText("Tamamlandı");
                }
            } else {
                super.setValue(value);
            }
        }
    }
