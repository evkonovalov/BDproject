package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    StageState stageState = StageState.Book;

    ObservableList<String> bookFilters = FXCollections.observableArrayList("By name",
            "By author", "By genre", "By genre and author");
    ObservableList<String> clientFilters = FXCollections.observableArrayList("By name",
            "By address", "By fine", "By date");
    ObservableList<String> orderFilters = FXCollections.observableArrayList("By client",
            "By date", "Overdue orders", "By book");

    Runtime rt = Runtime.getRuntime();

    ArrayList<String> def = new ArrayList<String>();
    @FXML
    CheckBox onlyActive;
    @FXML
    Button create;
    @FXML
    Button supButton1;
    @FXML
    Button supButton2;
    @FXML
    TableView<Order> orderTable;
    @FXML
    TableView<Book> bookTable;
    @FXML
    TableView<Client> clientTable;
    @FXML
    Button book;
    @FXML
    Button client;
    @FXML
    Button order;
    @FXML
    Button show;
    @FXML
    Label title;
    @FXML
    TableColumn<Book, String> bnameCol;
    @FXML
    TableColumn<Book, String> anameCol;
    @FXML
    TableColumn<Book, String> lnameCol;
    @FXML
    TableColumn<Book, Integer> qCol;

    @FXML
    TableColumn<Book, String> gnameCol;
    @FXML
    TableColumn<Book, String> cnameCol;
    @FXML
    TableColumn<Book, String> adrCol;
    @FXML
    TableColumn<Book, String> cInDateCol;
    @FXML
    TableColumn<Book, Double> fineCol;

    @FXML
    TableColumn<Book, String> bnameOrdCol;
    @FXML
    TableColumn<Book, String> cnameOrdCol;
    @FXML
    TableColumn<Book, String> lnameOrdCol;
    @FXML
    TableColumn<Book, String> dFromCol;
    @FXML
    TableColumn<Book, String> dToCol;
    @FXML
    TableColumn<Book, Boolean> activeCol;

    @FXML
    TextField key;
    @FXML
    Label keyLabel;
    @FXML
    TextField key2;
    @FXML
    Label keyLabel2;
    @FXML
    TextField key3;
    @FXML
    Label keyLabel3;
    @FXML
    ChoiceBox filter;


    private void setBooks() {
        title.setText("Books");
        stageState = StageState.Book;
        bookTable.setVisible(true);
        clientTable.setVisible(false);
        orderTable.setVisible(false);
        filter.setItems(bookFilters);
        show.setVisible(false);
        key.setVisible(false);
        keyLabel.setVisible(false);
        key2.setVisible(false);
        keyLabel2.setVisible(false);
        key3.setVisible(false);
        keyLabel3.setVisible(false);
        onlyActive.setVisible(false);
        supButton2.setVisible(false);
        supButton1.setVisible(true);
        supButton1.setText("Change Book Qnt");
    }

    private void setOrders() {
        title.setText("Orders");
        stageState = StageState.Order;
        bookTable.setVisible(false);
        clientTable.setVisible(false);
        orderTable.setVisible(true);
        filter.setItems(orderFilters);
        show.setVisible(false);
        key.setVisible(false);
        keyLabel.setVisible(false);
        key2.setVisible(false);
        keyLabel2.setVisible(false);
        key3.setVisible(false);
        keyLabel3.setVisible(false);
        onlyActive.setVisible(false);
        supButton2.setVisible(true);
        supButton1.setVisible(true);
        supButton1.setText("Close Order");
        supButton2.setText("Extend Order");
    }

    private void setClients() {
        title.setText("Clients");
        stageState = StageState.Client;
        bookTable.setVisible(false);
        clientTable.setVisible(true);
        orderTable.setVisible(false);
        filter.setItems(clientFilters);
        show.setVisible(false);
        key.setVisible(false);
        keyLabel.setVisible(false);
        key2.setVisible(false);
        keyLabel2.setVisible(false);
        key3.setVisible(false);
        keyLabel3.setVisible(false);
        onlyActive.setVisible(false);
        supButton2.setVisible(true);
        supButton1.setVisible(true);
        supButton1.setText("Edit Client");
        supButton2.setText("Remove Fine");
    }

    @FXML
    public void initialize() {
        def.add("python3");
        def.add("/home/cyberogg/IdeaProjects/TBD/db.py");
        def.add("library");
        def.add("postgres");
        def.add("postgres");
        def.add("5432");
        show.setVisible(false);
        key.setVisible(false);
        keyLabel.setVisible(false);
        key2.setVisible(false);
        keyLabel2.setVisible(false);
        key3.setVisible(false);
        keyLabel3.setVisible(false);
        Image playI = new Image("file:///home/cyberogg/IdeaProjects/TBD/res/book.png");
        ImageView iv1 = new ImageView(playI);
        iv1.setFitHeight(41);
        iv1.setFitWidth(41);
        book.setGraphic(iv1);
        playI = new Image("file:///home/cyberogg/IdeaProjects/TBD/res/client.png");
        iv1 = new ImageView(playI);
        iv1.setFitHeight(41);
        iv1.setFitWidth(41);
        client.setGraphic(iv1);
        playI = new Image("file:///home/cyberogg/IdeaProjects/TBD/res/order.png");
        iv1 = new ImageView(playI);
        iv1.setFitHeight(41);
        iv1.setFitWidth(41);
        order.setGraphic(iv1);

        bnameOrdCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        cnameOrdCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        lnameOrdCol.setCellValueFactory(new PropertyValueFactory<>("libraryName"));
        dFromCol.setCellValueFactory(new PropertyValueFactory<>("dateFrom"));
        dToCol.setCellValueFactory(new PropertyValueFactory<>("dateTo"));
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        cnameCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        adrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        cInDateCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        cnameCol.setSortType(TableColumn.SortType.DESCENDING);

        bnameCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        anameCol.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("libraryName"));
        qCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        gnameCol.setCellValueFactory(new PropertyValueFactory<>("genreName"));
        bnameCol.setSortType(TableColumn.SortType.DESCENDING);
        setBooks();

        filter.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                show.setVisible(true);
                key.setText("");
                key2.setText("");
                key3.setText("");
                if (stageState == StageState.Book) {
                    bookFilterUpdate(t1);
                }
                if (stageState == StageState.Client) {
                    clientFilterUpdate(t1);
                }
                if (stageState == StageState.Order) {
                    orderFilterUpdate(t1);
                }
            }
        });
    }

    private void orderFilterUpdate(Number t1){
        int type = t1.intValue();
        switch (type) {
            case 0:
                keyLabel.setText("Client Name");
                keyLabel2.setText("");
                keyLabel3.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                onlyActive.setVisible(true);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 1:
                keyLabel.setText("Date YYYY-MM-DD");
                keyLabel2.setText("");
                keyLabel3.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                onlyActive.setVisible(true);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 2:
                keyLabel.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                onlyActive.setVisible(false);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 3:
                keyLabel.setText("Book Name");
                keyLabel2.setText("");
                keyLabel3.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                onlyActive.setVisible(true);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
        }
    }

    private void clientFilterUpdate(Number t1){
        int type = t1.intValue();
        switch (type) {
            case 0:
                keyLabel.setText("Client Name");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 1:
                keyLabel.setText("Address");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 2:
                keyLabel.setText("Fine");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 3:
                keyLabel.setText("Date YYYY-MM-DD");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(false);
                keyLabel2.setVisible(false);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
        }
    }

    private void bookFilterUpdate(Number t1){
        int type = t1.intValue();
        switch (type) {
            case 0:
                keyLabel.setText("Book Name");
                keyLabel2.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(true);
                keyLabel2.setVisible(true);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 1:
                keyLabel.setText("Author");
                keyLabel2.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(true);
                keyLabel2.setVisible(true);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 2:
                keyLabel.setText("Genre");
                keyLabel2.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(true);
                keyLabel2.setVisible(true);
                key3.setVisible(false);
                keyLabel3.setVisible(false);
                break;
            case 3:
                keyLabel.setText("Author");
                keyLabel2.setText("Genre");
                keyLabel3.setText("Library");
                key.setVisible(true);
                keyLabel.setVisible(true);
                key2.setVisible(true);
                keyLabel2.setVisible(true);
                key3.setVisible(true);
                keyLabel3.setVisible(true);
                break;
        }
    }

    void createDialog(String label, String fxml) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(label);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void onOpenDialog(ActionEvent event) throws IOException {
        if(stageState == StageState.Book){
            createDialog("Add Book", "AddBookDialog.fxml");
        }
        if(stageState == StageState.Client){
            createDialog("Add Client", "AddClientDialog.fxml");
        }
        if(stageState == StageState.Order){
            createDialog("Add Order", "AddOrderDialog.fxml");
        }
    }

    void makeAlert(String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }

    void makeAnnounce(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void bookOnClick(ActionEvent event) {
        setBooks();
    }

    @FXML
    void orderOnClick(ActionEvent event) {
        setOrders();
    }

    @FXML
    void clientOnClick(ActionEvent event) {
        setClients();
    }
    @FXML
    void showOnClick(ActionEvent event) {
        if(stageState == StageState.Book){
            bookShowOnClick();
        }
        if(stageState == StageState.Client){
            clientShowOnClick();
        }
        if(stageState == StageState.Order){
            orderShowOnClick();
        }
    }

    private void clientShowOnClick() {
        String f = (String) filter.getSelectionModel().getSelectedItem();
        if (f.equals("By name")){
            clientFilterExec("get_clients_by_name");
        }
        if (f.equals("By address")){
            clientFilterExec("get_clients_by_addr");
        }
        if (f.equals("By fine")){
            clientFilterExec("get_clients_by_fine");
        }
        if (f.equals("By date")){
            clientFilterExec("get_clients_by_date");
        }
    }

    private void clientFilterExec(String method){
        String k1 = key.getText().trim();
        String k2 = key2.getText().trim();
        String k3 = key3.getText().trim();
        ArrayList<String> cmd = new ArrayList<>(def);
        cmd.add(method);
        String output;
        if(k1.equals("")){
            makeAlert("First key is empty");
            return;
        }
        else {
            cmd.add("1");
            cmd.add(k1);
        }
        String[] c = cmd.toArray(new String[cmd.size()]);
        try {
            Process p = rt.exec(c);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String s = null;
            ArrayList<Client> clients = new ArrayList<Client>();
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                if(!s.equals("")) {
                    clients.add(new Client(s));
                    count++;
                }
            }
            if(count > 0){
                makeAnnounce("Search found " + count + " lines.");
            } else {
                makeAlert("Nothing found.");
            }
            clientTable.setItems(FXCollections.observableList(clients));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void bookFilterExec(String method, boolean three){
        String k1 = key.getText().trim();
        String k2 = key2.getText().trim();
        String k3 = key3.getText().trim();
        ArrayList<String> cmd = new ArrayList<>(def);
        cmd.add(method);
        String output;
        if(k1.equals("")){
            makeAlert("First key is empty");
            return;
        }
        if(k2.equals("")){
            if(three) {
                makeAlert("Second key is empty");
                return;
            }
            cmd.add("1");
            cmd.add(k1);
        } else if (k3.equals("")){
            cmd.add("2");
            cmd.add(k1);
            cmd.add(k2);
        } else {
            cmd.add("3");
            cmd.add(k1);
            cmd.add(k2);
            cmd.add(k3);
        }
        String[] c = cmd.toArray(new String[cmd.size()]);
        try {
            Process p = rt.exec(c);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String s = null;
            ArrayList<Book> books = new ArrayList<Book>();
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                if(!s.equals("")) {
                    books.add(new Book(s));
                    count++;
                }
            }
            if(count > 0){
                makeAnnounce("Search found " + count + " lines.");
            } else {
                makeAlert("Nothing found.");
            }
            bookTable.setItems(FXCollections.observableList(books));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void bookShowOnClick(){
        String f = (String) filter.getSelectionModel().getSelectedItem();
        if (f.equals("By name")){
            bookFilterExec("get_books_by_name",false);
        }
        if (f.equals("By author")){
            bookFilterExec("get_books_by_author",false);
        }
        if (f.equals("By genre")){
            bookFilterExec("get_books_by_genre",false);
        }
        if (f.equals("By genre and author")){
            bookFilterExec("get_books_by_author_and_genre",true);
        }
    }

    void orderFilterExec(String method, boolean overdue){
        String k1 = key.getText().trim();
        String k2 = onlyActive.isSelected() ? "1" : "0";
        String k3 = key3.getText().trim();
        ArrayList<String> cmd = new ArrayList<>(def);
        cmd.add(method);
        String output;
        if(k1.equals("") && !overdue){
            makeAlert("First key is empty");
            return;
        }
        if (k3.equals("")) {
            cmd.add("2");
            cmd.add(k1);
            cmd.add(k2);
        } else {
            cmd.add("3");
            cmd.add(k1);
            cmd.add(k2);
            cmd.add(k3);
        }
        String[] c = cmd.toArray(new String[cmd.size()]);
        try {
            System.out.println(Arrays.toString(c));
            Process p = rt.exec(c);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String s = null;
            ArrayList<Order> orders = new ArrayList<Order>();
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                if(!s.equals("")) {
                    orders.add(new Order(s));
                    count++;
                }
            }
            if(count > 0){
                makeAnnounce("Search found " + count + " lines.");
            } else {
                makeAlert("Nothing found.");
            }
            orderTable.setItems(FXCollections.observableList(orders));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void orderShowOnClick(){
        String f = (String) filter.getSelectionModel().getSelectedItem();
        if (f.equals("By client")){
            System.out.println("KEK");
            orderFilterExec("get_orders_by_client",false);
        }
        if (f.equals("By date")){
            orderFilterExec("get_orders_by_date",false);
        }
        if (f.equals("Overdue orders")){
            orderFilterExec("get_overdue_orders",true);
        }
        if (f.equals("By book")){
            orderFilterExec("get_orders_by_book",false);
        }
    }
    @FXML
    void supButton1OnClick() throws IOException {
        if(stageState == StageState.Book){
            createDialog("Change Quantity", "ChangeBookQDialog.fxml");
        }
        if(stageState == StageState.Order){
            createDialog("Close Order", "CloseOrderDialog.fxml");
        }
        if(stageState == StageState.Client){
            createDialog("Edit Client", "EditClientDialog.fxml");
        }
    }
    @FXML
    void supButton2OnClick() throws IOException {
        if(stageState == StageState.Order){
            createDialog("Extend Order", "ExtendOrderDialog.fxml");
        }
        if(stageState == StageState.Client){
            createDialog("Remove Fine", "RemoveFineDialog.fxml");
        }
    }
}
