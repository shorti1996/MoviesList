
package com.liebert.lab002.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.liebert.lab002.Services.ThemoviedbService;
import com.liebert.lab002.Utils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Movie extends RealmObject {
    public static int dummyId = -999;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("adult")
    @Expose
    private Boolean adult;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("genre_ids")
    @Expose
    private RealmList<RealmInt> genreIds = null;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("title")
    @Expose
    private String title = "dummy";

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("popularity")
    @Expose
    private Double popularity;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    @SerializedName("video")
    @Expose
    private Boolean video;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("user_vote")
    @Expose
    private Double userVote = 0.d;

    @SerializedName("is_dummy")
    @Expose
    private boolean isDummy = false;

    @SerializedName("seen")
    @Expose
    private boolean seen = false;

    @SerializedName("modified")
    @Expose
    private boolean modified = false;

    @SerializedName("movieCredits")
    @Expose
    private Credits movieCredits = null;

    public Credits getMovieCredits() {
        return movieCredits;
    }

    public void setMovieCredits(Credits movieCredits) {
        this.movieCredits = movieCredits;
    }

    public boolean getIsDummy() {
        return isDummy;
    }

    public void setIsDummy(boolean isDummy) {
        this.isDummy = isDummy;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.modified = true;
        this.seen = seen;
    }

    public boolean getModified() {
        return this.modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setUserVote(Double userVote) {
        this.modified = true;
        this.userVote = userVote;
    }

    public Double getUserVote() {
        return userVote;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Movie) {
            return ((Movie) obj).getId().equals(this.getId());
        }
        return super.equals(obj);
    }

    public String getFirstGenre() {
        return Utils.translateGenre(Realm.getDefaultInstance(), getGenreIds().get(0).getInt());
    }

    public String getBackdropImageUri() {
        return ThemoviedbService.BACKDROP_IMAGE_ENDPOINT + getBackdropPath();
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<RealmInt> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(RealmList<RealmInt> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

}
