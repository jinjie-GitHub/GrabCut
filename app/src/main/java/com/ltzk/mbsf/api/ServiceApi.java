package com.ltzk.mbsf.api;

import com.ltzk.mbsf.base.BaseBean;
import com.ltzk.mbsf.bean.AuthorBean;
import com.ltzk.mbsf.bean.AuthorDetailBean;
import com.ltzk.mbsf.bean.AuthorFamous;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.FontListBean;
import com.ltzk.mbsf.bean.GalleryDetailBean;
import com.ltzk.mbsf.bean.GlyphsListBean;
import com.ltzk.mbsf.bean.HistoryBean;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.PayWeichatBean;
import com.ltzk.mbsf.bean.QQGroupBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.SimilarBean;
import com.ltzk.mbsf.bean.TmplListBean;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.VersionBean;
import com.ltzk.mbsf.bean.VideoListBean;
import com.ltzk.mbsf.bean.XeLoginBean;
import com.ltzk.mbsf.bean.ZiAuthorBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.bean.ZiTiesListBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.bean.ZitieShiwenBean;
import com.ltzk.mbsf.bean.ZuopinDetailBean;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 描述：retrofit的接口service定义
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */
public interface ServiceApi {


     //由原来的Observable<ResponseData<String>> 改成 Observable<Response<ResponseData<String>>>
    /*********************************  字典 ****************************************************/

    //作者列表
    @FormUrlEncoded
    @POST("glyph/authors")
    Observable<ResponseData<RowBean<ZiAuthorBean>>> glyph_authors(@Field("p") String p);

    //作者列表推荐
    @FormUrlEncoded
    @POST("author/sug")
    Observable<ResponseData<List<String>>> author_sug(@Field("p") String p);

    //作品搜索推荐
    @FormUrlEncoded
    @POST("zuopin/sug")
    Observable<ResponseData<List<String>>> zuopin_sug(@Field("p") String p);


    //字典列表
    @FormUrlEncoded
    @POST("glyph/query")
    Observable<ResponseData<RowBean<ZiBean>>> glyph_query(@Field("p") String p);

    //字典列表 集字选择
    @FormUrlEncoded
    @POST("glyph/picker")
    Observable<ResponseData<RowBean<ZiBean>>> glyph_picker(@Field("p") String p);

    //用户单字收藏（仅彩色字）
    @FormUrlEncoded
    @POST("glyph/fav")
    Observable<ResponseData<String>> glyph_fav_aid(@Field("p") String p);

    //取消单字收藏。
    @FormUrlEncoded
    @POST("glyph/unfav")
    Observable<ResponseData<String>> glyph_unfav_aid(@Field("p") String p);


    //收藏列表
    @FormUrlEncoded
    @POST("glyph/favlist")
    Observable<ResponseData<RowBean<ZiBean>>> glyph_favlist_hanzi(@Field("p") String p);


    /*********************************  字帖选辑 ****************************************************/

    //获取选辑列表
    @FormUrlEncoded
    @POST("gallery/list")
    Observable<ResponseData<ZitieHomeBean>> gallery_list(@Field("p") String p);

    //字帖列表
    @FormUrlEncoded
    @POST("zuopin/ztlist")
    Observable<ResponseData<RowBean<ZitieBean>>> zuopin_ztlist(@Field("p") String p);


    //获取选辑作品列表
    @FormUrlEncoded
    @POST("gallery/items")
    Observable<ResponseData<RowBean<ZitieHomeBean.ListBeanX.ListBeanAndType>>> gallery_items(@Field("p") String p);

    //获取朝代列表
    @FormUrlEncoded
    @POST("dylist")
    Observable<ResponseData<List<String>>> dylist(@Field("p") String p);

    //获取作者列表
    @FormUrlEncoded
    @POST("author/query")
    Observable<ResponseData<List<AuthorBean>>> author_query(@Field("p") String p);

    //获取作者作品列表
    @FormUrlEncoded
    @POST("author/zplist")
    Observable<ResponseData<RowBean<ZitieHomeBean.ListBeanX.ListBean>>> author_zplist(@Field("p") String p);


    /*//获取作者
    @FormUrlEncoded
    @POST("author/detail")
    Observable<ResponseData<AuthorDetailBean>> author_detail(@Field("p") String p);*/


    //获取搜索作品列表
    @FormUrlEncoded
    @POST("zuopin/query")
    Observable<ResponseData<RowBean<ZitieHomeBean.ListBeanX.ListBean>>> zuopin_query(@Field("p") String p);



    //获取作品详细
    @FormUrlEncoded
    @POST("zuopin/details")
    Observable<ResponseData<ZuopinDetailBean>> zuopin_details(@Field("p") String p);

    //获取作品集详细
    @FormUrlEncoded
    @POST("gallery/details")
    Observable<ResponseData<GalleryDetailBean>> gallery_details(@Field("p") String p);

    //获取作者详细
    @FormUrlEncoded
    @POST("author/details")
    Observable<ResponseData<AuthorDetailBean>> author_details(@Field("p") String p);



    /*********************************  字帖 ****************************************************/
    //字帖列表
    @FormUrlEncoded
    @POST("zitie/query")
    Observable<ResponseData<RowBean<ZitieBean>>> zitie_query(@Field("p") String p);

    //字帖详细
    @FormUrlEncoded
    @POST("zitie/details")
    Observable<ResponseData<ZitieBean>> zitie_details(@Field("p") String p);

    //字帖注解
    @FormUrlEncoded
    @POST("zitie/page/glyphs")
    Observable<ResponseData<List<ZiBean>>> zitie_page_glyphs(@Field("p") String p);

    //字帖注释文
    @FormUrlEncoded
    @POST("zitie/page/text")
    Observable<ResponseData<ZitieShiwenBean>> zitie_page_text(@Field("p") String p);

    //字典列表
    @FormUrlEncoded
    @POST("zitie/glyphs/query")
    Observable<ResponseData<List<ZiBean>>> zitie_glyph_query(@Field("p") String p);

    //字帖收藏
    @FormUrlEncoded
    @POST("zitie/page/fav")
    Observable<ResponseData<String>> zitie_page_fav(@Field("p") String p);

    //字帖收藏取消
    @FormUrlEncoded
    @POST("zitie/page/unfav")
    Observable<ResponseData<String>> zitie_page_unfav(@Field("p") String p);

    //字帖收藏批量取消
    @FormUrlEncoded
    @POST("zitie/page/unfavs")
    Observable<ResponseData<BaseBean>> zitie_page_unfavs(@Field("p") String p);

    //字帖收藏列表查询
    @FormUrlEncoded
    @POST("zitie/page/favlist")
    Observable<ResponseData<RowBean<ZitieBean>>> zitie_page_favlist(@Field("p") String p);

    //高清字帖
    @FormUrlEncoded
    @POST("zitie/hd/url")
    Observable<ResponseData<String>> zitie_hd_url(@Field("p") String p);


    /*********************************  集字 ****************************************************/
    /**
     * @brief 获取用户创建的集字列表。
     */
    @FormUrlEncoded
    @POST("jizi/list")
    Observable<ResponseData<RowBean<JiziBean>>> jizi_list(@Field("p") String p);

    /**
     * @brief 添加集字。
     */
    @FormUrlEncoded
    @POST("jizi/add")
    Observable<ResponseData<JiziBean>> jizi_add(@Field("p") String p);

    /**
     * @brief 获取集字的 json 数据。
     */
    @FormUrlEncoded
    @POST("jizi/json")
    Observable<ResponseData<String>> jizi_json(@Field("p") String p);

    /**
     * @brief 修改集字标题。
     */
    @FormUrlEncoded
    @POST("jizi/update/title")
    Observable<ResponseData<String>> jizi_update_title(@Field("p") String p);


    /**
     * @brief 修改集字数据 修改图片
     */
    @FormUrlEncoded
    @POST("jizi/update/json")
    Observable<ResponseData<List<String>>> jizi_update_json(@Field("p") String p);

    /**
     * @brief 修改集字内容 修改集字的字内容
     */
    @FormUrlEncoded
    @POST("jizi/update/text")
    Observable<ResponseData<JiziBean>> jizi_update_text(@Field("p") String p);

    /**
     * @brief 删除集字。
     */
    @FormUrlEncoded
    @POST("jizi/delete")
    Observable<ResponseData<String>> jizi_delete(@Field("p") String p);

    /**
     * @brief 更新序号。
     */
    @FormUrlEncoded
    @POST("jizi/update/order")
    Observable<ResponseData<String>> update_order(@Field("p") String p);

    /**
     * @brief 自动选字。
     */
    @FormUrlEncoded
    @POST("jizi/auto")
    Observable<ResponseData<JiziBean>> jizi_auto(@Field("p") String p);

    /********************************  字库******************************************************/

    /**
     * @brief 字库查询。
     */
    @FormUrlEncoded
    @POST("font/query")
    Observable<ResponseData<RowBean<ZilibBean>>> font_query(@Field("p") String p);

    /**
     * @brief 字库字形查询。
     */
    @FormUrlEncoded
    @POST("font/glyphs")
    Observable<ResponseData<RowBean<ZiLibDetailBean>>> font_glyphs(@Field("p") String p);

    /**
     * @brief 字库字形详细查询。
     */
    @FormUrlEncoded
    @POST("font/glyphs")
    Observable<ResponseData<RowBean<ZiBean>>> font_glyphs_detail(@Field("p") String p);

    /**
     * @brief 获取字符集列表
     */
    @FormUrlEncoded
    @POST("font/charset")
    Observable<ResponseData<List<String>>> font_charset(@Field("p") String p);

    /**
     * @brief 查询字形列表
     */
    @FormUrlEncoded
    @POST("font/glyphs/query")
    Observable<ResponseData<RowBean<ZiLibDetailBean>>> font_glyphs_query(@Field("p") String p);

    /**
     * @brief 获取指定字符的字形列表
     */
    @FormUrlEncoded
    @POST("font/glyphs/char")
    Observable<ResponseData<RowBean<ZiBean>>> font_glyphs_char(@Field("p") String p);

    /**
     * @brief 新建字库
     */
    @FormUrlEncoded
    @POST("font/add")
    Observable<ResponseData<ZilibBean>> font_add(@Field("p") String p);

    /**
     * @brief 更新字库
     */
    @FormUrlEncoded
    @POST("font/update")
    Observable<ResponseData<String>> font_update(@Field("p") String p);

    /**
     * @brief 删除字库
     */
    @FormUrlEncoded
    @POST("font/delete")
    Observable<ResponseData<String>> font_delete(@Field("p") String p);

    /**
     * @brief 字库详细
     */
    @FormUrlEncoded
    @POST("font/details")
    Observable<ResponseData<ZilibBean>> font_details(@Field("p") String p);


    /**
     * @brief 上传字形图片
     */
    @FormUrlEncoded
    @POST("font/glyphs/upload")
    Observable<ResponseData<ZiBean>> font_glyphs_upload(@Field("p") String p);

    /**
     * @brief 添加字形
     */
    @FormUrlEncoded
    @POST("font/glyphs/add")
    Observable<ResponseData<String>> font_glyphs_add(@Field("p") String p);

    /**
     * @brief 删除字形
     */
    @FormUrlEncoded
    @POST("font/glyphs/delete")
    Observable<ResponseData<String>> font_glyphs_delete(@Field("p") String p);

    /**
     * @brief 置顶
     */
    @FormUrlEncoded
    @POST("font/glyphs/prefer")
    Observable<ResponseData<String>> font_glyphs_prefer(@Field("p") String p);

    /**
     * @brief 更新已上传的字形
     */
    @FormUrlEncoded
    @POST("font/glyphs/update")
    Observable<ResponseData<String>> font_glyphs_update(@Field("p") String p);

    /**
     * @brief 字形详情
     */
    @FormUrlEncoded
    @POST("glyph/details")
    Observable<ResponseData<ZiBean>> glyphs_details(@Field("p") String p);



    /*********************************  用户 ****************************************************/
    /***
     * @brief 获取验证码。
     */
    @FormUrlEncoded
    @POST("user/get_code")
    Observable<ResponseData<String>> get_code(@Field("p") String p);

    /**
     * @brief 短信验证码登录。
     */
    @FormUrlEncoded
    @POST("user/login_code")
    Observable<ResponseData<UserBean>> login_code(@Field("p") String p);

    /**
     * @brief 用户名密码登录。
     */
    @FormUrlEncoded
    @POST("user/login_pwd")
    Observable<ResponseData<UserBean>> login_pwd(@Field("p") String p);

    /**
     * @brief 微信登录。
     */
    @FormUrlEncoded
    @POST("user/login_weixin")
    Observable<ResponseData<UserBean>> login_weixin(@Field("p") String p);

    //微信解绑
    @FormUrlEncoded
    @POST("user/unbind_weixin")
    Observable<ResponseData<String>> unbind_weixin(@Field("p") String p);

    //手机号解绑
    @FormUrlEncoded
    @POST("user/unbind_phone")
    Observable<ResponseData<String>> unbind_phone(@Field("p") String p);

    /**
     * @brief 用户注册
     */
    @FormUrlEncoded
    @POST("user/register")
    Observable<ResponseData<UserBean>> register(@Field("p") String p);

    /**
     * @brief 更新用户名密码。
     */
    @FormUrlEncoded
    @POST("user/update/pwd")
    Observable<ResponseData<String>> update_pwd(@Field("p") String p);

    //注销账号
    @FormUrlEncoded
    @POST("user/delete")
    Observable<ResponseData<String>> delete(@Field("p") String p);

    //退出登录
    @FormUrlEncoded
    @POST("user/logout")
    Observable<ResponseData<String>> logout(@Field("p") String p);

    //小鹅通用户退出登录
    @FormUrlEncoded
    @POST("xiaoe/sdk/logout")
    Observable<ResponseData<String>> xiaoe_sdk_logout(@Field("p") String p);

    //获取用户信息
    @FormUrlEncoded
    @POST("user/details")
    Observable<ResponseData<UserBean>> getinfo(@Field("p") String p);

    /**
     * @brief 修改昵称。
     */
    @FormUrlEncoded
    @POST("user/update/nickname")
    Observable<ResponseData<String>> update_nickname(@Field("p") String p);

    /**
     * @brief 设置头像。
     */
    @FormUrlEncoded
    @POST("user/update/avatar")
    Observable<ResponseData<UserBean>> update_avatar(@Field("p") String p);

    //用户签到
    @FormUrlEncoded
    @POST("user/checkin")
    Observable<ResponseData<JSONObject>> checkin(@Field("p") String p);

    /**
     * @brief 积分兑换。
     */
    @FormUrlEncoded
    @POST("user/redeem")
    Observable<ResponseData<UserBean>> redeem(@Field("p") String p);

    /**
     * @brief IAP验证。
     */
    @FormUrlEncoded
    @POST("user/iap/verify")
    Observable<ResponseData<String>> iap_verify(@Field("p") String p);

    /**
     * @brief 支付。
     */
    @FormUrlEncoded
    @POST("iap/wxpay/unifiedorder")
    Observable<ResponseData<PayWeichatBean>> iap_wxpay_unifiedorder(@Field("p") String p);

    /**
     * @brief 版本更新。
     */
    @FormUrlEncoded
    @POST("checkupdate")
    Observable<ResponseData<VersionBean>> checkupdate(@Field("p") String p);

    /**
     * @brief 发送一次性订阅消息
     */
    @FormUrlEncoded
    @POST("user/wxsubcribe")
    Observable<ResponseData<BaseBean>> user_wxsubcribe(@Field("p") String p);

    /**
     * 查询今日书论
     */
    @POST("shulun/today")
    Observable<ResponseData<TodayBean>> today();

    /**
     * 书法落款日历
     */
    @POST("calendar/today")
    Observable<ResponseData<String>> calendar();

    /**
     * 查询以往推送的书论
     */
    @FormUrlEncoded
    @POST("shulun/history")
    Observable<ResponseData<HistoryBean>> history(@Field("p") String p);

    /**
     * 字形详情
     */
    @FormUrlEncoded
    @POST("glyph/details")
    Observable<ResponseData<DetailsBean>> details(@Field("p") String p);

    /**
     * 字库查询
     */
    @FormUrlEncoded
    @POST("font/query")
    Observable<ResponseData<FontListBean>> query(@Field("p") String p);

    /**
     * 添加字形
     */
    @FormUrlEncoded
    @POST("font/glyphs/add")
    Observable<ResponseData<String>> add(@Field("p") String p);

    /**
     * 相似字形查询
     */
    @FormUrlEncoded
    @POST("glyph/similar")
    Observable<ResponseData<SimilarBean>> similar(@Field("p") String p);

    /**
     * 字库收藏
     */
    @FormUrlEncoded
    @POST("font/fav")
    Observable<ResponseData<Object>> libFav(@Field("p") String p);

    /**
     * 字库取消收藏
     */
    @FormUrlEncoded
    @POST("font/unfav")
    Observable<ResponseData<Object>> libUnFav(@Field("p") String p);

    /**
     * 删除字形
     */
    @FormUrlEncoded
    @POST("font/glyphs/delete")
    Observable<ResponseData<String>> glyphsDelete(@Field("p") String p);

    /**
     * 字帖单字查询
     */
    @FormUrlEncoded
    @POST("zitie/glyphs/query")
    Observable<ResponseData<GlyphsListBean>> glyphsQuery(@Field("p") String p);

    /**
     * 简体转繁体
     */
    @FormUrlEncoded
    @POST("s2t")
    Observable<ResponseData<String>> text_s2t(@Field("p") String p);

    /**
     * 繁体转简体
     */
    @FormUrlEncoded
    @POST("t2s")
    Observable<ResponseData<String>> text_t2s(@Field("p") String p);

    /**
     * 更新选字
     */
    @FormUrlEncoded
    @POST("jizi/update/gid")
    Observable<ResponseData<String>> updateGid(@Field("p") String p);

    /**
     * 获取文本
     */
    @FormUrlEncoded
    @POST("jizi/text")
    Observable<ResponseData<String>> getJiZiText(@Field("p") String p);

    /**
     * 清除选字
     */
    @FormUrlEncoded
    @POST("jizi/unset")
    Observable<ResponseData<String>> jiZiUnset(@Field("p") String p);

    /**
     * 设置样式属性
     */
    @FormUrlEncoded
    @POST("jizi/update/style")
    Observable<ResponseData<String>> jizi_update_style(@Field("p") String p);

    /**
     * 获取集字模板列表
     */
    @FormUrlEncoded
    @POST("jizi/tmpl/list")
    Observable<ResponseData<TmplListBean>> jizi_tmpl_list(@Field("p") String p);

    /**
     * 获取视频列表
     */
    @FormUrlEncoded
    @POST("glyph/video/query")
    Observable<ResponseData<VideoListBean>> glyph_video_query(@Field("p") String p);

    /**
     * 申请发布
     */
    @FormUrlEncoded
    @POST("glyph/video/publish")
    Observable<ResponseData<String>> glyph_video_publish(@Field("p") String p);

    /**
     * 撤销发布
     */
    @FormUrlEncoded
    @POST("glyph/video/unpublish")
    Observable<ResponseData<String>> glyph_video_unpublish(@Field("p") String p);

    /**
     * 公开
     */
    @FormUrlEncoded
    @POST("glyph/video/public")
    Observable<ResponseData<String>> glyph_video_public(@Field("p") String p);

    /**
     * 私密
     */
    @FormUrlEncoded
    @POST("glyph/video/private")
    Observable<ResponseData<String>> glyph_video_private(@Field("p") String p);

    /**
     * 删除
     */
    @FormUrlEncoded
    @POST("glyph/video/delete")
    Observable<ResponseData<String>> glyph_video_delete(@Field("p") String p);

    /**
     * 上传视频字节
     */
    /*@Multipart
    @POST("glyph/video/upload")
    Observable<ResponseData<VideoListBean.Videos>> glyph_video_upload(@Part("p") RequestBody p, @Part("video") RequestBody video);*/

    /**
     * 上传视频文件
     */
    @POST("glyph/video/upload")
    Observable<ResponseData<VideoListBean.Videos>> glyph_video_upload(@Body RequestBody body);

    /**
     * 推广文案
     */
    @FormUrlEncoded
    @POST("user/update/adtitle")
    Observable<ResponseData<String>> user_update_adtitle(@Field("p") String p);

    /**
     * 推广链接
     */
    @FormUrlEncoded
    @POST("user/update/adlink")
    Observable<ResponseData<String>> user_update_adlink(@Field("p") String p);

    /**
     * 字帖分组
     */
    @FormUrlEncoded
    @POST("glyph/video/author/zities")
    Observable<ResponseData<ZiTiesListBean>> glyph_video_author_zities(@Field("p") String p);

    /**
     * 字库分组
     */
    @FormUrlEncoded
    @POST("glyph/video/author/fonts")
    Observable<ResponseData<RowBean<ZilibBean>>> glyph_video_author_fonts(@Field("p") String p);

    /**
     * 获取字形列表
     */
    @FormUrlEncoded
    @POST("jizi/glyphs")
    Observable<ResponseData<List<List<DetailsBean>>>> jizi_glyphs(@Field("p") String p);

    /**
     * 视频收藏列表
     */
    @FormUrlEncoded
    @POST("glyph/video/favlist")
    Observable<ResponseData<VideoListBean>> glyph_video_favlist(@Field("p") String p);

    /**
     * 视频取消收藏
     */
    @FormUrlEncoded
    @POST("glyph/video/unfav")
    Observable<ResponseData<Object>> glyph_video_unfav(@Field("p") String p);

    /**
     * 视频收藏
     */
    @FormUrlEncoded
    @POST("glyph/video/fav")
    Observable<ResponseData<Object>> glyph_video_fav(@Field("p") String p);

    /**
     * 小鹅通登录
     */
    @FormUrlEncoded
    @POST("xiaoe/sdk/login")
    Observable<ResponseData<XeLoginBean>> user_sdk_login(@Field("p") String p);

    /**
     * 轮播图-发现
     */
    @Deprecated
    @FormUrlEncoded
    @POST("slider/items/discovery")
    Observable<ResponseData<List<BannerViewsInfo>>> slider_items_discovery(@Field("p") String p);

    /**
     * 轮播图-字典
     */
    @Deprecated
    @FormUrlEncoded
    @POST("slider/items/home")
    Observable<ResponseData<List<BannerViewsInfo>>> slider_items_home(@Field("p") String p);

    /**
     * 轮播图-字帖
     */
    @Deprecated
    @FormUrlEncoded
    @POST("slider/items/gallery")
    Observable<ResponseData<List<BannerViewsInfo>>> slider_items_gallery(@Field("p") String p);

    /**
     * 获取QQ客服群号及加群链接
     */
    @FormUrlEncoded
    @POST("qqun")
    Observable<ResponseData<QQGroupBean>> qqun(@Field("p") String p);

    /**
     * 获取发现页项目列表
     */
    @Deprecated
    @FormUrlEncoded
    @POST("disc/items")
    Observable<ResponseData<List<BannerViewsInfo>>> disc_items(@Field("p") String p);

    /**
     * 广告查询-新接口
     * 7ea63487	首页轮播图
     * 7f45f53d	发现页轮播图
     * 9ea5f71a	发现页项目列表
     */
    @FormUrlEncoded
    @POST("ad/query")
    Observable<ResponseData<List<BannerViewsInfo>>> ad_query(@Field("p") String p);

    /**
     * 作者收藏
     */
    @FormUrlEncoded
    @POST("author/fav")
    Observable<ResponseData<String>> author_fav(@Field("p") String p);

    /**
     * 取消作者收藏
     */
    @FormUrlEncoded
    @POST("author/unfav")
    Observable<ResponseData<String>> author_unfav(@Field("p") String p);

    /**
     * 作者收藏列表
     */
    @FormUrlEncoded
    @POST("author/favlist")
    Observable<ResponseData<List<String>>> author_favlist(@Field("p") String p);

    /**
     * 用户排序或删除收藏的作者
     */
    @FormUrlEncoded
    @POST("author/fav/update")
    Observable<ResponseData<String>> author_fav_update(@Field("p") String p);

    /**
     * 获取著名书法家列表
     */
    @FormUrlEncoded
    @POST("author/famous")
    Observable<ResponseData<AuthorFamous>> author_famous(@Field("p") String p);
}