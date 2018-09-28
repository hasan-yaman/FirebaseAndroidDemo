package yaman.hasan.hasandemo;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class YoutubeSearchUtils extends AsyncTask<Void, Void, List<Video>> {

    private String keyword;
    private YouTube youtube;
    private YouTube.Search.List searchQuery;
    private YouTube.Channels.List channelQuery;
    private YouTube.Videos.List videosQuery;


    public YoutubeSearchUtils(String keyword) {
        this.keyword = keyword;
        this.youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

            }
        }).setApplicationName("HasanDemo").build();

        try {
            searchQuery = youtube.search().list("id, snippet");
            searchQuery.setKey(Constans.KEY);
            searchQuery.setType("video");
            searchQuery.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url,snippet/thumbnails/default/url," +
                    "snippet/thumbnails/high/url,snippet/thumbnails/medium/url,snippet/thumbnails/standard/url,snippet/channelId)");

            channelQuery = youtube.channels().list("snippet");
            channelQuery.setKey(Constans.KEY);
            channelQuery.setFields("items(snippet/title, snippet/thumbnails/default/url, snippet/thumbnails/medium/url, snippet/thumbnails/high/url)");

            videosQuery = youtube.videos().list("snippet,statistics");
            videosQuery.setKey(Constans.KEY);
            videosQuery.setFields("items(snippet/publishedAt, statistics/viewCount)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<Video> doInBackground(Void... voids) {
        searchQuery.setQ(keyword);
        List<Video> videos = new ArrayList<>();
        try {
            SearchListResponse response = searchQuery.execute();
            List<SearchResult> results = response.getItems();

            for (SearchResult searchResult : results) {
                String id = searchResult.getId().getVideoId();
                String title = searchResult.getSnippet().getTitle();

                String thumbnailURL = searchResult.getSnippet().getThumbnails().getDefault().getUrl();
                if (searchResult.getSnippet().getThumbnails().getMaxres() != null) {
                    thumbnailURL = searchResult.getSnippet().getThumbnails().getMaxres().getUrl();
                } else if (searchResult.getSnippet().getThumbnails().getHigh() != null) {
                    thumbnailURL = searchResult.getSnippet().getThumbnails().getHigh().getUrl();
                } else if (searchResult.getSnippet().getThumbnails().getMedium() != null) {
                    thumbnailURL = searchResult.getSnippet().getThumbnails().getMedium().getUrl();
                } else if (searchResult.getSnippet().getThumbnails().getStandard() != null) {
                    thumbnailURL = searchResult.getSnippet().getThumbnails().getStandard().getUrl();
                }

                String channelID = searchResult.getSnippet().getChannelId();
                // Daha sonra channelID den title ve thumbnail bul
                String channelTitle = "";
                String channelThumbnailURL = "";
                channelQuery.setId(channelID);
                ChannelListResponse channelListResponse = channelQuery.execute();
                List<Channel> channelList = channelListResponse.getItems();
                for (Channel channel : channelList) {
                    channelTitle = channel.getSnippet().getTitle();
                    channelThumbnailURL = channel.getSnippet().getThumbnails().getDefault().getUrl();
                    if (channel.getSnippet().getThumbnails().getHigh() != null) {
                        channelThumbnailURL = channel.getSnippet().getThumbnails().getHigh().getUrl();
                    } else if (channel.getSnippet().getThumbnails().getMedium() != null) {
                        channelThumbnailURL = channel.getSnippet().getThumbnails().getMedium().getUrl();
                    }
                }

                String viewCount = "";
                String timeDifference = "";
                videosQuery.setId(id);
                VideoListResponse videoListResponse = videosQuery.execute();
                List<com.google.api.services.youtube.model.Video> videoList = videoListResponse.getItems();
                for (com.google.api.services.youtube.model.Video video : videoList) {

                    DateTime publishTime = video.getSnippet().getPublishedAt();

                    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    //simpleDateFormat.setTimeZone(TimeZone.getDefault());
                    Date publishDate = simpleDateFormat.parse(publishTime.toString());
                    Date currentDate = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));

                    long difference = currentDate.getTime() - publishDate.getTime();
                    timeDifference = calculateTimeDifference(difference);

                    viewCount = video.getStatistics().getViewCount().toString();
                }

                /*Log.i("Info","id -> " + id);
                Log.i("Info","title -> " + title);
                Log.i("Info","thumbnailURL -> " + thumbnailURL);
                Log.i("Info","channelTitle "  + channelTitle);
                Log.i("Info","channelThumbnailURL " + channelThumbnailURL);
                Log.i("Info","viewCount -> " + viewCount);
                Log.i("Info","timeDifference -> " + timeDifference); */

                videos.add(new Video(id, title, thumbnailURL, channelTitle, channelThumbnailURL, viewCount, timeDifference));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

    private String calculateTimeDifference(long difference) {
        Log.i("Info", "difference -> " + difference);
        if (difference < Constans.hoursInMilli) {
            return difference / Constans.minutesInMilli + " dakika önce";
        } else if (difference < Constans.daysInMilli) {
            return difference / Constans.hoursInMilli + " saat önce";
        } else if (difference < Constans.weeksInMilli) {
            return difference / Constans.daysInMilli + " gün önce";
        } else if (difference < Constans.monthsInMilli) {
            return difference / Constans.weeksInMilli + " hafta önce";
        } else if (difference < Constans.yearsInMilli) {
            return difference / Constans.monthsInMilli + " ay önce";
        } else {
            return difference / Constans.yearsInMilli + " yıl önce";
        }
    }

}
