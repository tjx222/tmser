package com.tmser.blog.utils.footnotes.internal;

import com.tmser.blog.utils.footnotes.FootnoteExtension;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.format.options.ElementPlacement;
import com.vladsch.flexmark.util.format.options.ElementPlacementSort;

public class FootnoteFormatOptions {

    public final ElementPlacement footnotePlacement;
    public final ElementPlacementSort footnoteSort;

    public FootnoteFormatOptions(DataHolder options) {
        footnotePlacement = FootnoteExtension.FOOTNOTE_PLACEMENT.get(options);
        footnoteSort = FootnoteExtension.FOOTNOTE_SORT.get(options);
    }
}
