//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.sns.bean;

import java.io.File;
import java.io.Serializable;

public class SnsShareContent implements Serializable {
    private static final long serialVersionUID = -480338281815885907L;
    private String title;
    private String url;
    private String content;
    private String image;
    private String comment;
    private String description;
    private String hideContent = "";
    private String qqWeiboHideContent = "";
    private String wapUrl;
    private File shareImgFile;

    public SnsShareContent() {
    }

    public File getShareImgFile() {
        return this.shareImgFile;
    }

    public void setShareImgFile(File shareImgFile) {
        this.shareImgFile = shareImgFile;
    }

    public String getWapUrl() {
        return this.wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQqWeiboHideContent() {
        return this.qqWeiboHideContent;
    }

    public void setQqWeiboHideContent(String qqWeiboHideContent) {
        this.qqWeiboHideContent = qqWeiboHideContent;
    }

    public String getHideContent() {
        return this.hideContent;
    }

    public void setHideContent(String hideContent) {
        this.hideContent = hideContent;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
