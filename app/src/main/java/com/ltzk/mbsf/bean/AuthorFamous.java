package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import java.util.List;

public class AuthorFamous extends BaseBean {
    public int total;
    public List<Author> list;

    public static final class Author {
        public String _name;
        public String _dynasty;
        public String _zpnum;
        public String _head;
    }
}