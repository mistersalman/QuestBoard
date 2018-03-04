package com.qbteam.questboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mark Spencer on 2/21/2018.
 */

public class QuestList extends AppCompatActivity {
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Button newQuest, backButton;
    ListView questList;
    ToggleButton toggleQuest;

    ArrayList<String> titles = new ArrayList<String>();;
    ArrayList<String> descriptions = new ArrayList<String>();;
    ArrayList<String> postID = new ArrayList<String>();

    private SearchView questSearch;
    ArrayList<ArrayList<String>> dataBaseTags = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> dataBaseToggleTags = new ArrayList<ArrayList<String>>();
    ArrayList<String> temp;
    ArrayList<String> titleList = new ArrayList();
    ArrayList<String> copyList = new ArrayList();
    ArrayList<String> idList = new ArrayList();
    ArrayList<String> idTempList = new ArrayList<>();
    ArrayList<String> copyIdList = new ArrayList();
    List<String> userPostID = new ArrayList<>();
    String tempString, tempSwap, tempName;
    int count, index, value;
    int max = 0;
    int[] matchList;
    boolean toggle = false;

    FirebaseAuth mobileAuth;
    FirebaseUser currentUser;
    QBUser user = new QBUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_list);

        backButton = (Button) findViewById(R.id.backButton);
        questList = (ListView) findViewById(R.id.itemList);
        questSearch = (SearchView) findViewById(R.id.questSearch);
        
        /*
        These are all the buttons, you should probably be able to see that pretty easy
         */
        newQuest = (Button) findViewById(R.id.newQuest);
        toggleQuest = (ToggleButton) findViewById(R.id.toggleQuest);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestList.super.onBackPressed();
            }
        });

        newQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(QuestList.this,
                        PostNewQuest.class);
                startActivity(createIntent);
                finish();
                //Go to account management page

            }
        });

        questList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("id view: ", copyIdList.get(position).replace("%40", "@"));
                String postPath = copyIdList.get(position).replace("%40", "@");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                databaseReference.child(postPath).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QBPost post = dataSnapshot.getValue(QBPost.class);
                        Log.d("values post object", dataSnapshot.toString());
                        Log.d("OP ID", post.getPosterID());
                        if(post.getPosterID().compareTo(mobileAuth.getUid().toString()) == 0)
                        {
                            Intent viewIntent = new Intent(QuestList.this, ViewPostedQuestEmployer.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("postID", copyIdList.get(position));
                            viewIntent.putExtras(bundle);
                            startActivity(viewIntent);
                            finish();
                        }
                        else
                        {
                            Intent viewIntent = new Intent(QuestList.this, ViewPostedQuestEmployee.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("postID", copyIdList.get(position));
                            viewIntent.putExtras(bundle);
                            startActivity(viewIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        questSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                List<String> searchTags = Arrays.asList(s.split("\\s*,\\s*"));

                count = 0;
                matchList = new int[dataBaseTags.size()];
                copyList = new ArrayList();
                copyIdList = new ArrayList();

                if(toggle)
                {
                    for(int v = 0; v < titleList.size(); v++)
                    {
                        copyList.add(titleList.get(v));
                        copyIdList.add(idTempList.get(v));
                    }

                    for(int x = 0; x < dataBaseToggleTags.size(); x++)
                    {
                        for(int y = 0; y < dataBaseToggleTags.get(x).size(); y++)
                        {
                            for(int z = 0; z < searchTags.size(); z++)
                            {
                                if(dataBaseToggleTags.get(x).get(y).equals(searchTags.get(z)))
                                {
                                    count++;
                                }
                            }
                        }
                        matchList[x] = count;
                        count = 0;
                    }
                }
                else
                {
                    for(int v = 0; v < titles.size(); v++)
                    {
                        copyList.add(titles.get(v));
                        copyIdList.add(idList.get(v));
                    }

                    for(int x = 0; x < dataBaseTags.size(); x++)
                    {
                        for(int y = 0; y < dataBaseTags.get(x).size(); y++)
                        {
                            for(int z = 0; z < searchTags.size(); z++)
                            {
                                if(dataBaseTags.get(x).get(y).equals(searchTags.get(z)))
                                {
                                    count++;
                                }
                            }
                        }
                        matchList[x] = count;
                        count = 0;
                    }
                }

                for(int t = 0; t < titleList.size(); t++)
                {
                    index = t;
                    for(int u = t; u < titleList.size();  u++)
                    {
                        if(matchList[u] > max)
                        {
                            max = matchList[u];
                            index = u;
                        }
                    }
                    tempSwap = copyList.get(t);
                    copyList.set(t, copyList.get(index));
                    copyList.set(index, tempSwap);

                    tempSwap = copyIdList.get(t);
                    copyIdList.set(t, copyIdList.get(index));
                    copyIdList.set(index, tempSwap);

                    count = matchList[t];
                    matchList[t] = matchList[index];
                    matchList[index] = count;
                    max = 0;

                }

                for (int i = copyList.size() - 1; i > 0; i--)
                {
                    if(matchList[i] == 0)
                    {
                        copyList.remove(i);
                        copyIdList.remove(i);
                    }
                }
                if(matchList[0] == 0)
                {
                    copyList.remove(0);
                    copyIdList.remove(0);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, android.R.layout.simple_list_item_1, copyList);
                questList.setAdapter(arrayAdapter);

                questSearch.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        toggleQuest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    copyIdList = new ArrayList<>();
                    for(int v = 0; v < idTempList.size(); v++)
                    {
                        copyIdList.add(idTempList.get(v));
                    }
                    toggle = true;
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, android.R.layout.simple_list_item_1, titleList);
                    questList.setAdapter(arrayAdapter);
                    questSearch.setQuery("", false);
                    questSearch.clearFocus();
                }
                else
                {
                    copyIdList = new ArrayList<>();
                    for(int v = 0; v < idList.size(); v++)
                    {
                        copyIdList.add(idList.get(v));
                    }
                    toggle = false;
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, android.R.layout.simple_list_item_1, titles);
                    questList.setAdapter(arrayAdapter);
                    questSearch.setQuery("", false);
                    questSearch.clearFocus();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        creating the questlist, also known as itemList from the xml file. I'm 90% sure that this is how you access it. The documentation on the ListView is actually god fucking awful. So, yaknow.
         */
        questList = (ListView) findViewById(R.id.itemList);
        final String path = "posts/";

        mobileAuth = FirebaseAuth.getInstance();
        currentUser = mobileAuth.getCurrentUser();
        String pathUser = "users/" + currentUser.getUid().toString() + "/";

        final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceUser = databaseUser.getReference();
        databaseReferenceUser.child(pathUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(QBUser.class);
                userPostID = user.getPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child(path).getChildren()) {
                    String key = (String) ds.getRef().toString().substring(40);

                    idList.add(key);
                    copyIdList.add(key);

                    titles.add(ds.child("/title").getValue(String.class));

                    temp = new ArrayList();
                    for (DataSnapshot j : ds.child("/tags/").getChildren())
                    {
                        tempString = j.getValue(String.class);
                        temp.add(tempString);
                    }
                    tempString = ds.child("/title").getValue(String.class);
                    //titleList.add(tempString);
                    dataBaseTags.add(temp);
                    for(int x = 0; x < userPostID.size(); x++)
                    {
                        if(userPostID.get(x).equals(ds.getKey()+"/"))
                        {
                            titleList.add(tempString);
                            dataBaseToggleTags.add(temp);
                            idTempList.add(key);
                        }
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuestList.this, android.R.layout.simple_list_item_1, titles);
                questList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
