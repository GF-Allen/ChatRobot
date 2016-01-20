package com.alen.chatrobot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView lvList;

    private ArrayList<ChatBean> chatDatas = new ArrayList<>();

    private int[] pic = new int[]{R.mipmap.p1, R.mipmap.p2, R.mipmap.p3,
            R.mipmap.p4, R.mipmap.p5, R.mipmap.p6};
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56951417");

        initUI();

        initData();

        initListener();
    }

    private void initUI() {
        lvList = (ListView) findViewById(R.id.lv_list);
    }

    private void initData() {
//        chatDatas.add(new ChatBean("吃饭吃饭", true, -1));
//        chatDatas.add(new ChatBean("美女哟", false, pic[1]));
//        chatDatas.add(new ChatBean("美女哟", false, -1));
    }

    private void initListener() {
        adapter = new ChatAdapter();
        lvList.setAdapter(adapter);
    }

    public void startSpeak(View v) {
        showDialog();
    }



    private StringBuffer buffer = new StringBuffer();

    private void showDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, mInitListener);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                //System.out.println("===="+recognizerResult.getResultString());
                String json = recognizerResult.getResultString();
                String data = parseData(json, true);
                buffer.append(data);
                if (isLast) {
                    String result = buffer.toString();
                    buffer = new StringBuffer();

                    //数据的刷新
                    refreshData(result,true);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Toast.makeText(MainActivity.this,"请重新说话",Toast.LENGTH_SHORT).show();
            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private void refreshData(String result, boolean isAsk) {

        int imageId = -1;

        if (isAsk) {
            //ask
            chatDatas.add(new ChatBean(result, true, imageId));
            adapter.notifyDataSetChanged();
            //answer
            //创建文本语义理解对象
            TextUnderstander mTextUnderstander = TextUnderstander.createTextUnderstander(this, null);
            //初始化监听器
            TextUnderstanderListener searchListener = new TextUnderstanderListener() {
                //语义结果回调
                public void onResult(UnderstanderResult result) {
                    //System.out.println("回答："+result.getResultString());
                    String data = parseData(result.getResultString(), false);
                    refreshData(data, false);
                }

                //语义错误回调
                public void onError(SpeechError error) {
                    System.out.println(error.getErrorDescription());
                }
            };
            //开始语义理解
            mTextUnderstander.understandText(result, searchListener);
        } else {

            if (TextUtils.isEmpty(result)) {
                return;
            }

            if (result.contains("美女") || result.contains("妹子")) {
                Random random = new Random();
                int i = random.nextInt(pic.length);
                imageId = pic[i];
            }

            chatDatas.add(new ChatBean(result, false, imageId));
            adapter.notifyDataSetChanged();
        }

        System.out.println("大小"+chatDatas.size());

    }

    private String parseData(String text,boolean isAsk) {

        StringBuffer sb = new StringBuffer();
        if (isAsk) {
            Gson gson = new Gson();
            VoiceBean result = gson.fromJson(text, VoiceBean.class);
            for (VoiceBean.WS ws : result.ws) {
                String w = ws.cw.get(0).w;
                sb.append(w);
            }
        } else {
            try {
                JSONObject object = new JSONObject(text);

                JSONObject answer = object.getJSONObject("answer");

                String result = answer.getString("text");
                sb.append(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                System.out.println("初始化失败，错误码：" + code);
            }
        }
    };

    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果，用户可参见附录13.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见Demo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            System.out.println(results.getResultString());
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {

        }

        //开始录音
        public void onBeginOfSpeech() {
        }

        //volume音量值0~30，data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
        }

        //结束录音
        public void onEndOfSpeech() {
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chatDatas.size();
        }

        @Override
        public ChatBean getItem(int position) {
            return chatDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(MainActivity.this, R.layout.adapter_chat, null);
                holder.tvAsker = (TextView) convertView.findViewById(R.id.tv_asker);
                holder.llAnswer = (LinearLayout) convertView.findViewById(R.id.ll_answer);
                holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChatBean chat = getItem(position);
            if (chat.isAsk()) {//发出的消息

                holder.llAnswer.setVisibility(View.GONE);
                holder.tvAsker.setVisibility(View.VISIBLE);
                holder.tvAsker.setText(chat.getContent());

            } else {//接收的消息

                holder.tvAsker.setVisibility(View.GONE);
                holder.llAnswer.setVisibility(View.VISIBLE);
                holder.tvAnswer.setText(chat.getContent());

                if (chat.getImageId() != -1) {
                    holder.ivImage.setImageResource(chat.getImageId());
                    holder.ivImage.setVisibility(View.VISIBLE);
                } else {
                    holder.ivImage.setVisibility(View.GONE);
                }

            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvAsker;
        LinearLayout llAnswer;
        TextView tvAnswer;
        ImageView ivImage;
    }
}
