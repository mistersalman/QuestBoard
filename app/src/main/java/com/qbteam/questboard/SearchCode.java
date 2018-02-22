/*
package com.qbteam.questboard;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QuestListSearchTest extends AppCompatActivity {

    private SearchView questSearch;
    ArrayList<ArrayList<String>> dataBaseTags = new ArrayList<ArrayList<String>>();
    ArrayList<String> temp;
    ArrayList<String> titleList = new ArrayList();
    ArrayList<String> copyList = new ArrayList();
    String tempString, tempSwap;
    int count, index;
    int max = 0;
    int[] matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list_search_test);

        questSearch = (SearchView) findViewById(R.id.questSearch);

        questSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                List<String> searchTags = Arrays.asList(s.split("\\s*,\\s*"));

                count = 0;
                matchList = new int[dataBaseTags.size()];
                copyList = new ArrayList();

                for(int v = 0; v < titleList.size(); v++)
                {
                    copyList.add(titleList.get(v));
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

                    count = matchList[t];
                    matchList[t] = matchList[index];
                    matchList[index] = count;
                    max = 0;

                }

                Toast.makeText(QuestListSearchTest.this, copyList.get(0)+String.valueOf(matchList[0])+"\n"+copyList.get(1)+String.valueOf(matchList[1])+"\n"+copyList.get(2)+String.valueOf(matchList[2]), Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference().child("posts/");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    //GenericTypeIndicator<ArrayList<String>> a = new GenericTypeIndicator<ArrayList<String>>(){};
                    temp = new ArrayList();
                    for (DataSnapshot j : i.child("/tags/").getChildren())
                    {
                        tempString = j.getValue(String.class);
                        temp.add(tempString);
                    }
                    tempString = i.child("/title").getValue(String.class);
                    titleList.add(tempString);
                    dataBaseTags.add(temp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
*/