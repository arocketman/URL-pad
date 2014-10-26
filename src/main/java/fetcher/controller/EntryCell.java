package fetcher.controller;

import fetcher.model.PageEntry;
import javafx.scene.control.ListCell;

public class EntryCell extends ListCell<PageEntry> {
    @Override
    protected void updateItem(PageEntry item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){
            setText(item.getName() + " , " + item.getDescription());
        }
    }
}
