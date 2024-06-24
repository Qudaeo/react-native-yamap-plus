package ru.vvdev.yamap.suggest;

import javax.annotation.Nullable;

public class MapSuggestItem {
    private String _searchText;
    private String _title;

    @Nullable
    private String _subTitle;

    @Nullable
    private String _uri;

    public MapSuggestItem() {
    }

    public String getSearchText() {
        return _searchText;
    }

    public void setSearchText(String searchText) {
        _searchText = searchText;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    @Nullable
    public String getSubtitle() {
        return _subTitle;
    }

    public void setSubtitle(@Nullable String subtitle) {
        _subTitle = subtitle;
    }

    @Nullable
    public String getUri() {
        return _uri;
    }

    public void setUri(@Nullable String uri) {
        _uri = uri;
    }
}
