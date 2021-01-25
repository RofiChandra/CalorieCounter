package com.example.caloriecounter_vol2.ui.categories;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.caloriecounter_vol2.DBAdapter;
import com.example.caloriecounter_vol2.MainActivity;
import com.example.caloriecounter_vol2.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link categoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class categoriesFragment extends Fragment {

    /* Variables */
    private Cursor listCursor;
    private View mainView;

    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    private String currentID;
    private String gcurrentName;

    // Fragment Variables (Necessary)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    // Constructor
    public categoriesFragment() {
        // Required empty public constructor
    }



    public static categoriesFragment newInstance(String param1, String param2) {
        categoriesFragment fragment = new categoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*----------On Create----------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        /*-----Set Title-----*/
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        // Inflate the layout for this fragment
        return mainView;
    }

    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /*- on Activity Created --------------------------------------------------------- */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate the list of categories
        populateList("0", ""); // Parent

        setHasOptionsMenu(true);
    } // onActivityCreated


    // On create option Menu
    // Creating action icon on toolbar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu
        //MenuInflater menuInflater = ((MainActivity)getActivity()).getMenuInflater();
       //menuInflater.inflate(R.menu.menu_categories, menu);
        ((MainActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_categories, menu);

        menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemDelete = menu.findItem(R.id.action_delete);



        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.action_add) {
            createNewCategory();
        }
        if (id == R.id.action_edit) {
            editCategory();
        }
        if (id == R.id.action_delete) {
            deleteCategory();
        }
        return super.onOptionsItemSelected(menuItem);
    }




    /*- populate List -------------------------------------------------------------- */
    public void populateList(String parentID, String parentName){

        /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Get categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursor = db.select("categories", fields, "category_parent_id", parentID, "category_name", "ASC");

        // Createa a array
        ArrayList<String> values = new ArrayList<String>();

        // Convert categories to string
        int categoriesCount = listCursor.getCount();
        for(int x=0;x<categoriesCount;x++){
            values.add(listCursor.getString(listCursor.getColumnIndex("category_name")));

            /* Toast.makeText(getActivity(),
                    "Id: " + categoriesCursor.getString(0) + "\n" +
                            "Name: " + categoriesCursor.getString(1), Toast.LENGTH_SHORT).show();*/
            listCursor.moveToNext();
        }


        // Close cursor
        // categoriesCursor.close();

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);

        // Set Adapter
        ListView lv = (ListView)getActivity().findViewById(R.id.listViewCategories);
        lv.setAdapter(adapter);

        // OnClick
        if(parentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    listItemClicked(arg2);
                }
            });
        }

        // Close db
        db.close();


        // Show/Hide Edit button
        if(parentID.equals("0")){
            // Remove edit button
            //Item menuItemEdit = (Item) getActivity().findViewById(R.id.action_edit);


                //menuItemEdit.setVisible(false);
                //menuItemDelete.setVisible(false);

        }
        else{
            // Show edt button
            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);
        }

    } // populateList

    /*- List item clicked ------------------------------------------------------------ */
    public void listItemClicked(int listItemIDClicked){

        // Move cursor to ID clicked
        listCursor.moveToPosition(listItemIDClicked);

        // Get ID and name from cursor
        String id = listCursor.getString(0);
        String name = listCursor.getString(1);
        String parentID = listCursor.getString(2);

        // Change title
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(name);

        // Move to sub class
        populateList(id, name);
    } // listItemClicked

    public void createNewCategory(){
        /* Change layout */
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);


        /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        /* Fill spinner with categories */
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        // Creating array
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount+1];

        // This is parent
        arraySpinnerCategories[0] = "-";

        // Convert Cursor to String
        for(int x=1;x<dbCursorCount+1;x++){
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();
            dbCursor.moveToNext();
        }

        // Populate spinner
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoryParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);



        /* SubmitButton listener */
        Button buttonHome = (Button)getActivity().findViewById(R.id.buttonCategoriesSubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCategoryOnSubmitOnClick();
            }
        });

        /* Close db */
        db.close();

    }




    public void createNewCategoryOnSubmitOnClick() {
        /* Database */
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Error?
        int error = 0;

        // Name
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }

        // Parent
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinnerCategoryParent);
        String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();

        String parentID;
        if(stringSpinnerCategoryParent.equals("-")){
            parentID = "0";
        }
        else{
            // Find we want to find parent ID from the text
            String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);
            String fields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerCategoryParentSQL);
            parentID = findParentID.getString(0).toString();


        }

        if(error == 0){
            // Ready variables
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            // Insert into database
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.insert("categories", "_id, category_name, category_parent_id", input);

            // Give feedback
            Toast.makeText(getActivity(), "Category created", Toast.LENGTH_LONG).show();

            // Move user back to correct design
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new categoriesFragment(), categoriesFragment.class.getName()).commit();

        }

        /* Close db */
        db.close();
    }

    // Edit Category
    public void editCategory() {
        Toast.makeText(getActivity(), "You want to edit food", Toast.LENGTH_SHORT).show();

    }


    // Delete Category
    public void deleteCategory() {

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}