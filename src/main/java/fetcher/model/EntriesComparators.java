package fetcher.model;

import java.util.Comparator;

/**
 * Created by Andrea on 12/11/2014.
 */
public class EntriesComparators {

    public static Comparator<PageEntry> getDateComparator(){
        return new Comparator<PageEntry>() {
            @Override
            public int compare(PageEntry o1, PageEntry o2) {
                return (int)(o1.getDateAdded().getTime() - o2.getDateAdded().getTime());
            }
        };
    }

    public static Comparator<PageEntry> getAlphabeticalComparator(){
        return new Comparator<PageEntry>() {
            @Override
            public int compare(PageEntry o1, PageEntry o2) {
                if(o1.getName().isEmpty())
                    return 1;
                else if(o2.getName().isEmpty())
                    return -1;
                return o1.getName().toLowerCase().trim().replace("\u00A0","").charAt(0) - o2.getName().toLowerCase().trim().replace("\u00A0","").charAt(0);
            }
        };
    }
}
