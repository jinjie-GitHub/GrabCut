package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * 描述：
 * 作者： on 2019-10-8 19:00
 * 邮箱：499629556@qq.com
 */
public class ZitieHomeBean extends BaseBean {


    //首页数据类型
    public static final String type_author = "author";
    public static final String type_zuopin = "zuopin";
    public static final String type_gallery = "gallery";
    public static final String type_zitie = "zitie";
    public static final String type_hd = "hd";

    /**
     * total : 3
     * list : [{"type":"author","title":"书法家","list":[{"_name":"王羲之","_dynasty":"晋","_head":"https://mbres.ygsf.com/author/2eb5d6a8dd17f935a233997c51ce6e49.jpg?u=024d"},{"_name":"王献之","_dynasty":"晋","_head":"https://mbres.ygsf.com/author/12cf4762db5cbfded0c3a6aac0ba9465.jpg?u=d86e"},{"_name":"颜真卿","_dynasty":"唐","_head":"https://mbres.ygsf.com/author/686e62f530f952a6798ebf818a92a410.jpg?u=26f9"},{"_name":"柳公权","_dynasty":"唐","_head":"https://mbres.ygsf.com/author/efe1c7f962bf49030f24035ab656b460.jpg?u=b71d"},{"_name":"欧阳询","_dynasty":"唐","_head":"https://mbres.ygsf.com/author/d78f1ff465534b82a16f2c148bafb1b1.jpg?u=fb1c"},{"_name":"苏轼","_dynasty":"宋","_head":"https://mbres.ygsf.com/author/76129a76953c2b7eb9802c5c45d7c5b2.jpg?u=d634"},{"_name":"黄庭坚","_dynasty":"宋","_head":"https://mbres.ygsf.com/author/a731746848fba0d0899a845eda283362.jpg?u=cb6a"},{"_name":"米芾","_dynasty":"宋","_head":"https://mbres.ygsf.com/author/98fc09d886c0d556683aac6ce0c14338.jpg?u=be92"},{"_name":"蔡襄","_dynasty":"宋","_head":"https://mbres.ygsf.com/author/e93ff990737e0cdbd6bc3ce54557c149.jpg?u=d76b"},{"_name":"赵孟頫","_dynasty":"元","_head":"https://mbres.ygsf.com/author/af1cec649ab95b5c63847e71ca5be717.jpg?u=aaa1"},{"_name":"文征明","_dynasty":"明","_head":"https://mbres.ygsf.com/author/9651ad946a6990d6795bf3b98cb4ea23.jpg?u=4892"},{"_name":"祝允明","_dynasty":"明","_head":"https://mbres.ygsf.com/author/39ccf4f7c08698a4004f725986fbaca4.jpg?u=98b4"},{"_name":"王铎","_dynasty":"明","_head":"https://mbres.ygsf.com/author/b98d4efd530dda855b7cb55b3049cc63.jpg?u=d36c"},{"_name":"董其昌","_dynasty":"明","_head":"https://mbres.ygsf.com/author/987bfb49cb942c89182ed2605a2ad36a.jpg?u=cb19"}]},{"type":"zuopin","title":"篆书典籍","list":[{"_id":"96d2f4b62a6e062a055f5ee1b8291a8e","_name":"说文字原考略","_author":"吴照","_cover_url":"https://mbres.ygsf.com/zitie/Z1/9a63958381d8ce7903d9ab8f0b542166/thumb/1-e21f.jpg"},{"_id":"4a0b9ad351626b23d7bcea1d555b6720","_name":"说文字原集注","_author":"蒋和","_cover_url":"https://mbres.ygsf.com/zitie/Z1/8040dfd818ca63ee7186845e3198f71b/thumb/1-9e73.jpg"},{"_id":"f693b463891780237c7e278119741745","_name":"说文解字真本","_author":"许慎","_cover_url":"https://mbres.ygsf.com/zitie/Z1/5f02edb8909ed6991975e7de2cbfd0db/thumb/1-3a3e.jpg"},{"_id":"640428e0a90fa2d510d9ee16a7ae0f3c","_name":"说文解字五音韵谱","_author":"李焘","_cover_url":"https://mbres.ygsf.com/zitie/Z1/7da39f3b789481490eb5af2795224b30/thumb/1-3b02.jpg"},{"_id":"bba571a180e6048fd03e802a5315117d","_name":"说文广义","_author":"王夫之","_cover_url":"https://mbres.ygsf.com/zitie/Z1/370d37f7310b1ffa0efd6b4f10c062ed/thumb/1-0f85.jpg"},{"_id":"65b466caf9c98aedc7842cd9f0bcb160","_name":"钦定篆书四书六经","_author":"李光地","_cover_url":"https://mbres.ygsf.com/zitie/Z1/070aa0691596d5c7e448e79518252557/thumb/1-b3b2.jpg"},{"_id":"bbe7f2f8e480c80a8dd059a1bfe28fd7","_name":"千文六书统要","_author":"胡正言","_cover_url":"https://mbres.ygsf.com/zitie/Z1/424a68c91d5621a5c3a839a576025b47/thumb/1-5e55.jpg"},{"_id":"8384126a2b482dc1d583da1fad692c3a","_name":"六书总要","_author":"吴元满","_cover_url":"https://mbres.ygsf.com/zitie/Z1/92d284a77740f697aed61cbc48775837/thumb/1-7c71.jpg"},{"_id":"21212b9697e639aec6a4985438dafdcf","_name":"六书正讹","_author":"周伯琦","_cover_url":"https://mbres.ygsf.com/zitie/Z1/4d38bd89a52b64bb5010a5fffd7ff266/thumb/1-f2ed.jpg"},{"_id":"89dae9a6836f326ffce59a0acc8710c6","_name":"六书统","_author":"杨桓","_cover_url":"https://mbres.ygsf.com/zitie/Z1/ad4e67545891b28cc1fb43c866b8e324/thumb/1-c0cf.jpg"},{"_id":"6d7a7a37f2a7f6c9788c8bac041409cc","_name":"六书通","_author":"闵齐伋","_cover_url":"https://mbres.ygsf.com/zitie/Z1/61bdafc2108748b05d41279789083023/thumb/1-8c89.jpg"},{"_id":"097c64ad7d142626661c084d823f10cc","_name":"金石韵府","_author":"朱云","_cover_url":"https://mbres.ygsf.com/zitie/Z1/59b57dd0249354ee8d9f5fd3b1907d3b/thumb/1-e3f4.jpg"},{"_id":"7da460edcec223e409bdb7b0b4c485aa","_name":"洪武正韵","_author":"朱厚煐","_cover_url":"https://mbres.ygsf.com/zitie/Z1/a2cdfbb4e94343400dd1424babf7ac57/thumb/1-73e8.jpg"},{"_id":"41c2377d622988176ad0bc241e80a565","_name":"汗简","_author":"郭忠恕","_cover_url":"https://mbres.ygsf.com/zitie/Z1/51fb64d0764be0a62208482b2b0fe3e0/thumb/1-c6fd.jpg"},{"_id":"71e2c0bc06577c69fe5bc8d10ee6acaa","_name":"汉印分韵选集","_author":"袁日省","_cover_url":"https://mbres.ygsf.com/zitie/Z1/a7a9cbbc17735f04dd1374249bc22b0a/thumb/1-a693.jpg"},{"_id":"1ad27c7ec51653127e2ef1fd876474e0","_name":"汉印分韵续集","_author":"孟昭鸿","_cover_url":"https://mbres.ygsf.com/zitie/Z1/41df5b6980687b33e9a883a1b2af0fd4/thumb/1-dbc1.jpg"},{"_id":"a94ca068d67d90feec7f19b5f0c23fdd","_name":"广金石韵府","_author":"林尚葵","_cover_url":"https://mbres.ygsf.com/zitie/Z1/21cce906bca28a00585633dfdaf8b896/thumb/1-191a.jpg"},{"_id":"1c6eb2b98c1d651c6a34981fc73a9e20","_name":"篆书正","_author":"戴明说","_cover_url":"https://mbres.ygsf.com/zitie/Z1/bbb0ff2da01e56d6a7162bd8f8c6601a/thumb/1-ff63.jpg"},{"_id":"2a5dde4e2baf57ee07b4c964abfa2c1f","_name":"篆书百韵歌","_author":"近代人","_cover_url":"https://mbres.ygsf.com/zitie/Z1/4d6fdc956b590a88d817f7cdf3167eac/thumb/1-a1da.jpg"},{"_id":"be20ec36d0348dff1baf577ad57eb206","_name":"篆法百韵歌","_author":"近代人","_cover_url":"https://mbres.ygsf.com/zitie/Z1/fcea4daefb7998e215dfe8e4b45cb5d3/thumb/1-f946.jpg"},{"_id":"aec067770b252efaf92a493f5d17c22b","_name":"钟鼎字源","_author":"汪立名","_cover_url":"https://mbres.ygsf.com/zitie/Z1/3a58018c9b56b81b45906039ccd96219/thumb/1-124e.jpg"},{"_id":"4da00bc24348a9e64297718b7f77d445","_name":"御制盛京赋","_author":"乾隆","_cover_url":"https://mbres.ygsf.com/zitie/Z1/b6d37d98278d2898197c78403b3a1e2a/thumb/1-561b.jpg"},{"_id":"c7a435fb980d0df5bed0464583d7fb98","_name":"偏类六书通","_author":"清朝人","_cover_url":"https://mbres.ygsf.com/zitie/Z1/d7231a13a74d4aef1f1ff24a84678f50/thumb/1-3ff5.jpg"}]},{"type":"gallery","title":"作品选辑","list":[{"_id":"00a766ee694317033300f257e474c84b","_title":"法帖大全","_cover_url":"https://mbres.ygsf.com/gallery/00a766ee694317033300f257e474c84b.jpg?u=675e"},{"_id":"6d62856863d6bf84fb30ecb49e5c1d9a","_title":"魏碑精选","_cover_url":"https://mbres.ygsf.com/gallery/6d62856863d6bf84fb30ecb49e5c1d9a.jpg?u=75fc"},{"_id":"8ede3e04c0c540422558ebdfe9761289","_title":"真迹选辑","_cover_url":"https://mbres.ygsf.com/gallery/8ede3e04c0c540422558ebdfe9761289.jpg?u=f884"},{"_id":"fd49f56472cf7702b727b583fe213706","_title":"明清书札","_cover_url":"https://mbres.ygsf.com/gallery/fd49f56472cf7702b727b583fe213706.jpg?u=6c82"},{"_id":"21a3c28dc4b2d176244b27405ee37d7b","_title":"汉碑经典","_cover_url":"https://mbres.ygsf.com/gallery/21a3c28dc4b2d176244b27405ee37d7b.jpg?u=854e"},{"_id":"a920653980fa201cb838e1b33f8a35b1","_title":"名家千字文","_cover_url":"https://mbres.ygsf.com/gallery/a920653980fa201cb838e1b33f8a35b1.jpg?u=854e"}]}]
     */

    private int total;
    private List<ListBeanX> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBeanX> getList() {
        return list;
    }

    public void setList(List<ListBeanX> list) {
        this.list = list;
    }

    public class ListBeanX extends BaseBean{
        private String _gallery_id;
        private int _total;
        private String _title;
        private List<ListBeanAndType> _list;

        public String get_gallery_id() {
            return _gallery_id;
        }

        public void set_gallery_id(String _gallery_id) {
            this._gallery_id = _gallery_id;
        }

        public int get_total() {
            return _total;
        }

        public void set_total(int _total) {
            this._total = _total;
        }

        public String get_title() {
            return _title;
        }

        public void set_title(String _title) {
            this._title = _title;
        }

        public List<ListBeanAndType> get_list() {
            return _list;
        }

        public void set_list(List<ListBeanAndType> _list) {
            this._list = _list;
        }

        public class ListBeanAndType extends BaseBean{
            private ListBean _data;//数据
            private String _type; //类型：gallery, zuopin, zitie, author

            //app添加用来做数据显示
            private String title;
            private int adapterType;//1


            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getAdapterType() {
                return adapterType;
            }

            public void setAdapterType(int adapterType) {
                this.adapterType = adapterType;
            }

            public ListBean get_data() {
                return _data;
            }

            public void set_data(ListBean _data) {
                this._data = _data;
            }

            public String get_type() {
                return _type;
            }

            public void set_type(String _type) {
                this._type = _type;
            }
        }

        public class ListBean extends BaseBean{
            /**
             * _name : 王羲之
             * _dynasty : 晋
             * _head : https://mbres.ygsf.com/author/2eb5d6a8dd17f935a233997c51ce6e49.jpg?u=024d
             */

            private String  _zitie_id;//作者 作品集里

            //书法名家  type=author
            private String _name;
            private String _dynasty;
            private String _head;

            //最新上线 、 热门推荐  type=zuopin
            private String _author;
            private String _cover_url;
            private String _zuopin_id;
            //private String _name;

            // 作品选辑 type=gallery
            //private String _cover_url;
            private String _gallery_id;
            private String _title;

            //字帖
            public int _video;
            private int _focus_page;
            private String _subtitle;
            private String _hd;
            private String _hd_url;
            private String _free;  //是否为免费字帖 1是。
            public String get_name() {
                return _name;
            }

            public void set_name(String _name) {
                this._name = _name;
            }

            public String get_dynasty() {
                return _dynasty;
            }

            public void set_dynasty(String _dynasty) {
                this._dynasty = _dynasty;
            }

            public String get_head() {
                return _head;
            }

            public void set_head(String _head) {
                this._head = _head;
            }

            public String get_author() {
                if(_author == null){
                    return "";
                }
                return _author;
            }

            public void set_author(String _author) {
                this._author = _author;
            }

            public String get_cover_url() {
                return _cover_url;
            }

            public void set_cover_url(String _cover_url) {
                this._cover_url = _cover_url;
            }


            public String get_title() {
                return _title;
            }

            public void set_title(String _title) {
                this._title = _title;
            }

            public String get_zitie_id() {
                return _zitie_id;
            }

            public void set_zitie_id(String _zitie_id) {
                this._zitie_id = _zitie_id;
            }

            public int get_focus_page() {
                return _focus_page;
            }

            public void set_focus_page(int _focus_page) {
                this._focus_page = _focus_page;
            }

            public String get_subtitle() {
                return _subtitle;
            }

            public void set_subtitle(String _subtitle) {
                this._subtitle = _subtitle;
            }

            public String get_zuopin_id() {
                return _zuopin_id;
            }

            public void set_zuopin_id(String _zuopin_id) {
                this._zuopin_id = _zuopin_id;
            }

            public String get_gallery_id() {
                return _gallery_id;
            }

            public void set_gallery_id(String _gallery_id) {
                this._gallery_id = _gallery_id;
            }

            public String get_hd() {
                return _hd;
            }

            public void set_hd(String _hd) {
                this._hd = _hd;
            }

            public String get_hd_url() {
                return _hd_url;
            }

            public void set_hd_url(String _hd_url) {
                this._hd_url = _hd_url;
            }

            public String get_free() {
                return _free;
            }

            public void set_free(String _free) {
                this._free = _free;
            }
        }
    }
}
