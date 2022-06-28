package com.tmser.blog.utils.footnotes.internal;

import com.tmser.blog.utils.footnotes.FootnoteExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;

public class FootnoteOptions {

    final String footnoteRefPrefix;
    final String footnoteRefSuffix;
    final String footnoteBackRefString;
    final String footnoteLinkRefClass;
    final String footnoteBackLinkRefClass;
    final int contentIndent;

    public FootnoteOptions(DataHolder options) {
        this.footnoteRefPrefix = FootnoteExtension.FOOTNOTE_REF_PREFIX.get(options);
        this.footnoteRefSuffix = FootnoteExtension.FOOTNOTE_REF_SUFFIX.get(options);
        this.footnoteBackRefString = FootnoteExtension.FOOTNOTE_BACK_REF_STRING.get(options);
        this.footnoteLinkRefClass = FootnoteExtension.FOOTNOTE_LINK_REF_CLASS.get(options);
        this.footnoteBackLinkRefClass = FootnoteExtension.FOOTNOTE_BACK_LINK_REF_CLASS.get(options);
        this.contentIndent = Parser.LISTS_ITEM_INDENT.get(options);
    }
}
