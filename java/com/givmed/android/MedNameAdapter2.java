//package com.givmed.android;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AlphabetIndexer;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//import android.widget.SectionIndexer;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//public class MedNameAdapter2 extends BaseAdapter implements SectionIndexer {
//
//    private static final int TYPE_HEADER = 1;
//    private static final int TYPE_NORMAL = 0;
//
//    private static final int TYPE_COUNT = 2;
//
////    A helper class for adapters that implement the SectionIndexer interface. If the items in the adapter are sorted by
////    simple alphabet-based sorting, then this class provides a way to do fast indexing of large lists using binary search.
////    It caches the indices that have been determined through the binary search and also invalidates the cache if changes
////    occur in the cursor.Your adapter is responsible for updating the cursor by calling setCursor(Cursor) if the cursor
////    changes. getPositionForSection(int) method does the binary search for the starting index of a given section (alphabet).
//
//    private AlphabetIndexer indexer;
//
//    private int[] usedSectionNumbers;
//
//    //A Map is a data structure consisting of a set of keys and values in which each key is mapped to a single value
//
//    private Map<Integer, Integer> sectionToOffset;
//    private Map<Integer, Integer> sectionToPosition;
//
//    public final List<MedName> mItems = new ArrayList<MedName>();
//    private final Context mContext;
//
//    public MedNameAdapter2(Context context) {
//        mContext = context;
//        DBHandler db = new DBHandler(context);
//        String selectQuery = "SELECT  * FROM " + DBHandler.TABLE_MEDS + " ORDER BY " + DBHandler.KEY_NAME;
//        SQLiteDatabase db1 = db.getWritableDatabase();
//        Cursor c = db1.rawQuery(selectQuery, null);
//        //final Cursor c = db1.query(DBHandler.TABLE_MEDS, null, null,
//          //      null, null, null, DBHandler.KEY_NAME + " ASC" );
//        //Log.e("cursor rows", "" + c.getCount());
//
//        if (c.moveToFirst()) {
//            do {
//
//
//                String cnt = c.getString(1);
//                Log.e("cursor", "" + cnt);
//
//            } while (c.moveToNext());
//            c.close();
//        }
//        indexer = new AlphabetIndexer(c, c.getColumnIndexOrThrow(DBHandler.KEY_NAME), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
//        int test = indexer.getSectionForPosition(9);
//        Log.e("test", "" + test);
//
//        sectionToPosition = new TreeMap<Integer, Integer>(); //use a TreeMap because we are going to iterate over its keys in sorted order
//        sectionToOffset = new HashMap<Integer, Integer>();
//
//        //final int count = mItems.size();
//        final int count = 10;
//        Log.e("count", "" + count);
//
//
//        int i;
//        //temporarily have a map alphabet section to first index it appears
//        //(this map is going to be doing somethine else later)
//        for (i = count - 1 ; i >= 0; i--){
//            Log.e("kaka","thn katsame"+i);
//
//            sectionToPosition.put(indexer.getSectionForPosition(i), i);
//        }
////The keySet() method is used to get a Set view of the keys contained in this map.
//        i = 0;
//        usedSectionNumbers = new int[sectionToPosition.keySet().size()];
//
//        //note that for each section that appears before a position, we must offset our
//        //indices by 1, to make room for an alphabetical header in our list
//        for (Integer section : sectionToPosition.keySet()){
//            sectionToOffset.put(section, i);
//            usedSectionNumbers[i] = section;
//            i++;
//        }
//
//        //use offset to map the alphabet sections to their actual indicies in the list
//        for(Integer section: sectionToPosition.keySet()){
//            sectionToPosition.put(section, sectionToPosition.get(section) + sectionToOffset.get(section));
//        }
//    }
////Given the index of a section within the array of section objects,
//// returns the starting position of that section within the adapter.
//
//    //he AlphabetIndexer will give us the position of the first word starting with each letter in our data set
//    @Override
//    public int getPositionForSection(int section) {
//        if (! sectionToOffset.containsKey(section)){
//            //This is only the case when the FastScroller is scrolling,
//            //and so this section doesn't appear in our data set. The implementation
//            //of Fastscroller requires that missing sections have the same index as the
//            //beginning of the next non-missing section (or the end of the the list if
//            //if the rest of the sections are missing).
//            //So, in pictorial example, the sections D and E would appear at position 9
//            //and G to Z appear in position 11.
//            int i = 0;
//            int maxLength = usedSectionNumbers.length;
//
//            //linear scan over the sections (constant number of these) that appear in the
//            //data set to find the first used section that is greater than the given section, so in the
//            //example D and E correspond to F
//            while (i < maxLength && section > usedSectionNumbers[i]){
//                i++;
//            }
//            if (i == maxLength) return getCount(); //the given section is past all our data
//
//            return indexer.getPositionForSection(usedSectionNumbers[i]) + sectionToOffset.get(usedSectionNumbers[i]);
//        }
//
//        return indexer.getPositionForSection(section) + sectionToOffset.get(section);
//    }
//
////Given a position within the adapter, returns the index of the corresponding section within the array of section objects.  @Override
//    public int getSectionForPosition(int position) {
//        int i = 0;
//        int maxLength = usedSectionNumbers.length;
//
//        //linear scan over the used alphabetical sections' positions
//        //to find where the given section fits in
//        while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])){
//            i++;
//        }
//        return usedSectionNumbers[i-1];
//    }
//// Returns an array of objects representing sections of the list.
//    @Override
//    public Object[] getSections() {
//        return indexer.getSections();
//    }
//    //nothing much to this: headers have positions that the sectionIndexer manages.
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == getPositionForSection(getSectionForPosition(position))){
//            return TYPE_HEADER;
//        }
//        return TYPE_NORMAL;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return TYPE_COUNT;
//    }
//
//    //these two methods just disable the headers
//    @Override
//    public boolean areAllItemsEnabled() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled(int position) {
//        if (getItemViewType(position) == TYPE_HEADER){
//            return false;
//        }
//        return true;
//    }
//
//
//    public void add(MedName item) {
//        mItems.add(item);
//        notifyDataSetChanged();
//    }
//
//    public void remove(Object item) {
//        mItems.remove(item);
//        notifyDataSetChanged();
//    }
//
//    // Clears the list adapter of all items.
//    public void clear() {
//        mItems.clear();
//        notifyDataSetChanged();
//    }
//
//    // Returns the number of Medicine Name
//    @Override
//    public int getCount() {
//        return mItems.size() + usedSectionNumbers.length;
//    }
//
//    // Retrieve the number of the Medicine Name
//    @Override
//    public Object getItem(int pos) {
//        return mItems.get(pos);
//    }
//
//    // Get the ID for the Medicine Name
//    // In this case it's just the position
//    @Override
//    public long getItemId(int pos) {
//        return pos;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) { //called after setAdapter,notifyDataSetChanged
//        final MedName name = (MedName) getItem(position);
//        String num = "";
//
//        // from medName.xml
//        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
//                R.layout.med_name, parent, false);
//
//        // Fill in specific MedName data
//        // Remember that the data that goes in this View
//        // corresponds to the user interface elements defined
//        // in the layout file
//
//        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
//        if (!name.getCount().equals("1")) num = " (" + name.getCount() + ")";
//        titleView.setText(name.getName() + num);
//
//        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
//        dateView.setText(name.getDate());
//
//        // Return the View you just created
//        return itemLayout;
//    }
//
//
//}
