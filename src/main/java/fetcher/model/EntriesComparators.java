package fetcher.model;

import java.util.Comparator;

/**
 * This utility class is used to return a comparator for two PageEntry instances.
 * The date comparator sorts by the date added, the alphabetical comparator sorts by the title of the entry.
 */
public class EntriesComparators {

    public static Comparator<PageEntry> getDateComparator(){
        return new Comparator<PageEntry>() {
            @Override
            public int compare(PageEntry o1, PageEntry o2) {
                //Converting the dates to milliseconds and getting the difference.
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
                //Bring it to lowercase, removing spaces, removing non-breaking spaces, getting the first character.
                return o1.getName().toLowerCase().trim().replace("\u00A0","").charAt(0) - o2.getName().toLowerCase().trim().replace("\u00A0","").charAt(0);
            }
        };
    }
}
