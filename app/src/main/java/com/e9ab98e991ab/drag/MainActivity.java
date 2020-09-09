package com.e9ab98e991ab.drag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View.OnClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "list click " + v.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(this, -1));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new DemoAdapter());
        DragMenuRelativeLayout rootLayout = (DragMenuRelativeLayout) findViewById(R.id.tumblr_frame_layout);


        rootLayout.setClickListener(new DragMenuRelativeLayout.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(MainActivity.this, "menu click"+position, Toast.LENGTH_LONG).show();
            }
        });
    }

    class DemoAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_item, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.itemView.setTag(i);
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(clickListener);
        }
    }




}
