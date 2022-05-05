package cn.knet.seal.vo;

import lombok.Data;

@Data
public class ResultMsg {
    private  int code;
    private  String msg;
    private  String pageCharset;
    private  String title;
    private  String keyword;
    private  String description;
    private  String pageCnt;

    public ResultMsg() {
    }

    public ResultMsg(int code, String msg, String pageCharset, String title, String keyword, String description, String pageCnt) {
        this.code = code;
        this.msg = msg;
        this.title = title;
        this.keyword = keyword;
        this.description = description;
        this.pageCnt = pageCnt;
        this.pageCharset = pageCharset;
    }
}
