package com.zskx.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hrv.startpack.R;
import com.zskx.activitys.Insert;
import com.zskx.activitys.Report;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.zskx.util.FileUtils.copyDBToSDcrad;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private MySqliteHelper mySqliteHelper;
    private List<PersonModel> list;
    private Cursor reportData;
    private final String VALUE_ID = "_id";
    private final String VALUE_NAME = "name";
    private final String VALUE_ISBOY = "isboy";
    private final String VALUE_AGE = "age";
    private final String VALUE_NUMBER = "number";
    private final String VALUE_ANSWER = "answer";
    private final String VALUE_HRVDATA1 = "hrvdata1";
    private final String VALUE_HRVDATA2 = "hrvdata2";
    private final String VALUE_GENESCORE = "genescore";
    private final String VALUE_DATA = "data";
    private final String VALUE_TITLE = "title";
    private final String VALUE_STATUS = "status";
    public RecyclerAdapter(Context context, List<PersonModel> list, MySqliteHelper mySqliteHelper) {
        this.context = context;
        this.list = list;
        this.mySqliteHelper = mySqliteHelper;
    }

    public void setList(List<PersonModel> list) {
        this.list = list;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_person, null);
        RecyclerAdapter.ViewHolder viewHolder = new RecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        final PersonModel model = list.get(position);
        holder.name.setText("名字：" + model.getName());
        if (model.getIsBoy() == 1) {
            holder.sex.setText("性别：男");
        } else {
            holder.sex.setText("性别：女");
        }
        holder.id.setText("编号：" + model.getId());
        holder.age.setText("年龄：" + model.getAge());
        holder.number.setText("手机号：" + model.getNumber());
        holder.question.setText("测试项目:"+model.getTitle());
        final PersonModel personModel = new PersonModel();
        personModel.setId(model.getId());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("确定要删除吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mySqliteHelper.deletePersonData(personModel);
                                list.clear();
                                list.addAll(mySqliteHelper.queryAllPersonData());
                                ToastUtils.show(context, "删除数据成功");
                                copyDBToSDcrad();
                                RecyclerAdapter.this.notifyDataSetChanged();
                            }
                        }).show();
            }
        });
        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportData = mySqliteHelper.queryPersonData((int) model.getId());
                if(reportData.getString(reportData.getColumnIndex(VALUE_STATUS)).equals("N")){
                    ToastUtils.show(context, "用户未测!");
                    return;
                }
                Intent intent = new Intent(context, Report.class);
                JSONObject json = new JSONObject();
                try {
                    json.put(VALUE_ID,reportData.getInt(reportData.getColumnIndex(VALUE_ID)));
                    json.put(VALUE_NAME,reportData.getString(reportData.getColumnIndex(VALUE_NAME)));
                    json.put(VALUE_ISBOY,reportData.getInt(reportData.getColumnIndex(VALUE_ISBOY)));
                    json.put(VALUE_AGE,reportData.getInt(reportData.getColumnIndex(VALUE_AGE)));
                    json.put(VALUE_NUMBER,reportData.getString(reportData.getColumnIndex(VALUE_NUMBER)));
                    json.put(VALUE_ANSWER,reportData.getString(reportData.getColumnIndex(VALUE_ANSWER)));
                    json.put(VALUE_HRVDATA1,reportData.getString(reportData.getColumnIndex(VALUE_HRVDATA1)));
                    json.put(VALUE_HRVDATA2,reportData.getString(reportData.getColumnIndex(VALUE_HRVDATA2)));
                    json.put(VALUE_GENESCORE,reportData.getString(reportData.getColumnIndex(VALUE_GENESCORE)));
                    json.put(VALUE_STATUS,reportData.getString(reportData.getColumnIndex(VALUE_STATUS)));
                    json.put(VALUE_DATA,reportData.getString(reportData.getColumnIndex(VALUE_DATA)));
                    json.put(VALUE_TITLE,reportData.getString(reportData.getColumnIndex(VALUE_TITLE)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putString("report", json.toString());
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
        holder.updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Insert.class);
                Bundle bundle = new Bundle();
                JSONObject json = new JSONObject();
                try {
                    json.put(VALUE_ID,model.getId());
                    json.put(VALUE_NAME,model.getName());
                    json.put(VALUE_AGE,model.getAge());
                    json.put(VALUE_NUMBER,model.getNumber());
                    json.put(VALUE_ISBOY,model.getIsBoy());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundle.putString("userInfo", String.valueOf(json));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView sex;
        private TextView age;
        private TextView id;
        private TextView number;
        private Button delete,updates,report;
        private TextView question;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            sex = (TextView) itemView.findViewById(R.id.sex);
            age = (TextView) itemView.findViewById(R.id.age);
            number = (TextView) itemView.findViewById(R.id.number);
            id = (TextView) itemView.findViewById(R.id.id);
            delete = (Button) itemView.findViewById(R.id.delete);
            report = (Button) itemView.findViewById(R.id.getReport);
            question = (TextView) itemView.findViewById(R.id.question);
            updates = (Button) itemView.findViewById(R.id.updates);
        }
    }
}
