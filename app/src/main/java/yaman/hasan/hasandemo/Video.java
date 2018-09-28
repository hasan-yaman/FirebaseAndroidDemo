package yaman.hasan.hasandemo;

/**
 * Created by hasanyaman on 4.09.2018.
 */

public class Video {
    private String id;
    private String title;
    private String thumbnailURL;
    private String channelTitle;
    private String channelThumbnailURL;
    private String viewCount;
    private String timeDifference;

    public Video(String id, String title, String thumbnailURL,String channelTitle, String channelThumbnailURL
    ,String viewCount, String timeDifference) {
        this.id = id;
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.channelTitle = channelTitle;
        this.channelThumbnailURL = channelThumbnailURL;
        this.viewCount = viewCount;
        this.timeDifference = timeDifference;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelThumbnailURL() {
        return channelThumbnailURL;
    }

    public void setChannelThumbnailURL(String channelThumbnailURL) {
        this.channelThumbnailURL = channelThumbnailURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
