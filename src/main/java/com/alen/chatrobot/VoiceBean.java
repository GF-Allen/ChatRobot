package com.alen.chatrobot;

import java.util.ArrayList;

/**
 * Created by Alen on 2016/1/13.
 */
public class VoiceBean {
    public ArrayList<WS> ws;

    public class WS {
        public ArrayList<CW> cw;

        @Override
        public String toString() {
            return "WS{" +
                    "cw=" + cw +
                    '}';
        }
    }

    public class CW {
        public String w;

        @Override
        public String toString() {
            return "CW{" +
                    "w='" + w + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VoiceBean{" +
                "ws=" + ws +
                '}';
    }
}
