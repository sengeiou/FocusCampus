package com.sam.globalRentalCar.http.net;


import com.sam.globalRentalCar.bean.FansBean;
import com.sam.globalRentalCar.bean.UserProductionOrLoveBean;
import com.sam.globalRentalCar.bean.VideoListBean;
import com.sam.globalRentalCar.http.request.CancelOrderRequestBean;
import com.sam.globalRentalCar.http.request.ConfirmOrderRequestBean;
import com.sam.globalRentalCar.http.request.DeleteVideoRequestBean;
import com.sam.globalRentalCar.http.request.HomeVideoLikeRequestBean;
import com.sam.globalRentalCar.http.request.LoginRequestBean;
import com.sam.globalRentalCar.http.request.LoginWithAccountRequestBean;
import com.sam.globalRentalCar.http.request.ModifyMessageRequestBean;
import com.sam.globalRentalCar.http.request.PayOrderRequestBean;
import com.sam.globalRentalCar.http.request.UpLoadVideoRequestBean;
import com.sam.globalRentalCar.http.request.VideoCommentRequestBean;
import com.sam.globalRentalCar.http.request.GetUpLoadImageRequestBean;
import com.sam.globalRentalCar.http.request.upLoadAfterRequestBean;
import com.sam.globalRentalCar.http.response.CommentListBean;
import com.sam.globalRentalCar.http.response.ConfirmOrderResponseBean;
import com.sam.globalRentalCar.http.response.DeleteVideoResponseBean;
import com.sam.globalRentalCar.http.response.FindUserListBean;
import com.sam.globalRentalCar.http.response.FollowResponseBean;
import com.sam.globalRentalCar.http.response.ForgetPassword;
import com.sam.globalRentalCar.http.response.GetCarBrandListResponseBean;
import com.sam.globalRentalCar.http.response.GetCarListResponseBean;
import com.sam.globalRentalCar.http.response.GetCarTypeListResponseBean;
import com.sam.globalRentalCar.http.response.GetRentalCarHomeMessageResponseBean;
import com.sam.globalRentalCar.http.response.GetUserConfirmInfoResponseBean;
import com.sam.globalRentalCar.http.response.GetUserCouponListResponseBean;
import com.sam.globalRentalCar.http.response.GetUserHomePagerMessageResponseBean;
import com.sam.globalRentalCar.http.response.HomeVideoLikeResponseBean;
import com.sam.globalRentalCar.http.response.LoginBean;
import com.sam.globalRentalCar.http.response.ModifyMessageResponseBean;
import com.sam.globalRentalCar.http.response.OrderDetailResponseBean;
import com.sam.globalRentalCar.http.response.OrderListResponseBean;
import com.sam.globalRentalCar.http.response.PayOrderResponseBean;
import com.sam.globalRentalCar.http.response.SwitchResponseBean;
import com.sam.globalRentalCar.http.response.UpLoadVideoResponseBean;
import com.sam.globalRentalCar.http.response.VerficationCodeBean;
import com.sam.globalRentalCar.http.response.VideoCommentResponseBean;
import com.sam.globalRentalCar.http.response.GetUpLoadImageResponseBean;
import com.sam.globalRentalCar.http.response.upLoadAfterResponseBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * ?????????????????????
 */
public interface RetrofitApi {
    /**
     * ???????????????
     */
    @GET(NetApiConstants.VERFICATION_CODE)
    Call<VerficationCodeBean> loadVerficationCode(@Query("phone") String phone);

    /**
     * ??????????????????????????????????????????
     *
     * @param loginRequestBean
     * @return
     */
    @POST(NetApiConstants.USER_LOGIN)
    Call<LoginBean> loadLogin(@Body LoginRequestBean loginRequestBean);

    /**
     * ????????????????????????
     *
     * @param loginWithAccountRequestBean
     * @return
     */
    @POST(NetApiConstants.LOGIN_WITH_ACCOUNT)
    Call<LoginBean> loginWithAccount(@Body LoginWithAccountRequestBean loginWithAccountRequestBean);

    /**
     * ????????????
     *
     * @param loginWithAccountRequestBean
     * @return
     */
    @POST(NetApiConstants.RESET_USER_PASSWORD)
    Call<ForgetPassword> forgetLoginPassword(@Body LoginWithAccountRequestBean loginWithAccountRequestBean);

    /**
     * ????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_VIDEO_LIST)
    Call<VideoListBean> loadHomeVideoListData(@Header("token") String header, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    /**
     * ?????????????????????????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_FOLLOWED_VIDEO_LIST)
    Call<VideoListBean> loadHomeFollowedVideoListData(@Header("token") String header, @Query("page") int page);

    /**
     * ??????????????????
     */
    @GET(NetApiConstants.GET_FOCUS)
    Call<FansBean> getFocus(@Query("userId") String userId, @Query("page") String page);

    /**
     * ????????????
     */
    @GET(NetApiConstants.FOCUS_USER)
    Call<FollowResponseBean> FocusUser(@Header("token") String header, @Query("userId") String userId, @Query("follow") String follow);

    /**
     * ??????????????????
     */
    @GET(NetApiConstants.GET_FANS)
    Call<FansBean> getFans(@Query("userId") String userId, @Query("page") String page);

    /**
     * ????????????????????????
     */
    @GET(NetApiConstants.GET_PERSONAL_PRODUCTION)
    Call<UserProductionOrLoveBean> getPersonalProduction(@Query("userId") String userId, @Query("page") String page);

    /**
     * ??????????????????????????????
     */
    @GET(NetApiConstants.GET_PERSONAL_LOVE)
    Call<UserProductionOrLoveBean> getPersonalLove(@Query("userId") String userId, @Query("page") String page);

    /**
     * ?????????????????????????????????
     */
    @GET(NetApiConstants.GET_COMMENT_LIST)
    Call<CommentListBean> getCommentList(@Query("videoId") String videoId, @Query("pageIndex") String pageIndex, @Query("pageSize") String pageSize);

    /**
     * ??????????????????
     *
     * @param videoCommentRequestBean
     * @return
     */
    @POST(NetApiConstants.POST_VIDEO_COMMON)
    Call<VideoCommentResponseBean> postVideoCommon(@Body VideoCommentRequestBean videoCommentRequestBean);

    /**
     * ?????????????????????
     *
     * @param videoCommentRequestBean
     * @return
     */
    @POST(NetApiConstants.POST_VIDEO_LIKE)
    Call<HomeVideoLikeResponseBean> postVideoLike(@Body HomeVideoLikeRequestBean videoCommentRequestBean);

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @POST(NetApiConstants.PRE_UPLOAD)
    Call<UpLoadVideoResponseBean> getUpLoadVideoParams(@Header("token") String token, @Body UpLoadVideoRequestBean upLoadVideoRequestBean);

    /**
     * ?????????????????????
     *
     * @return
     */
    @POST(NetApiConstants.AFTER_UPLOAD)
    Call<upLoadAfterResponseBean> UpLoadVideoAfter(@Header("token") String token, @Body upLoadAfterRequestBean upLoadAfterRequestBean);

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @POST(NetApiConstants.IMAGE_PRE_UPLOAD)
    Call<GetUpLoadImageResponseBean> getUpLoadPictureParams(@Header("token") String token, @Body GetUpLoadImageRequestBean getUpLoadImageRequestBean);

    /**
     * ???????????????????????????
     *
     * @return
     */
    @POST(NetApiConstants.MODIFY_MESSAGE)
    Call<ModifyMessageResponseBean> modifyPersonalMessageParams(@Header("token") String token, @Body ModifyMessageRequestBean modifyMessageRequestBean);

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_USER_HOME_MESSAGE)
    Call<GetUserHomePagerMessageResponseBean> getPersonalHomeMessageParams(@Query("userId") String userId);

    /**
     * ???????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_CAR_TYPE_LIST)
    Call<GetCarTypeListResponseBean> getCarTypeList();

    /**
     * ???????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_CAR_BRAND_LIST)
    Call<GetCarBrandListResponseBean> getCarBrandList();

    /**
     * ???????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_RENTAL_CAR_HOME_MESSAGE)
    Call<GetRentalCarHomeMessageResponseBean> getRentalCarHomeMessage(@Query("cityCode") String cityCode);

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_CAR_LIST)
    Call<GetCarListResponseBean> getCarList(@Query("brandIds") String brandIds, @Query("pickUpId") int pickUpId, @Query("startDate") String startDate, @Query("endDate") String endDate, @Query("carType") Integer carType, @Query("brandId") String brandId, @Query("carId") Long carId, @Query("order") Integer order);

    /**
     * ?????????????????????????????????(?????????????????????)
     *
     * @return
     */
    @GET(NetApiConstants.GET_USER_COUPON_LIST)
    Call<GetUserCouponListResponseBean> getUserCouponList(@Header("token") String token);

    /**
     * ???????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_USER_ORDER_CONFIRM_INFO)
    Call<GetUserConfirmInfoResponseBean> getUserOrderConfirmInfo(@Header("token") String token, @Query("carId") String carId, @Query("days") String days);

    /**
     * ????????????????????????
     *
     * @return
     */
    @POST(NetApiConstants.GET_USER_CONFIRM_ORDER)
    Call<ConfirmOrderResponseBean> getUserConfirmOrder(@Header("token") String token, @Body ConfirmOrderRequestBean confirmOrderRequestBean);

    /**
     * ??????????????????
     *
     * @return OrderListResponseBean
     */
    @GET(NetApiConstants.GET_USER_ORDER_MESSAGE)
    Call<OrderListResponseBean> getUserOrderListInfo(@Query("userId") String userId, @Query("status") int status);

    /**
     * ???????????????????????????
     *
     * @return OrderListResponseBean
     */
    @GET(NetApiConstants.GET_USER_ORDER_DETAIL)
    Call<OrderDetailResponseBean> getUserOrderDetail(@Query("userId") String userId, @Query("orderCode") String orderCode);

    /**
     * ????????????
     *
     * @return OrderListResponseBean
     */
    @POST(NetApiConstants.CANCEL_USER_ORDER)
    Call<OrderListResponseBean> cancelUserOrder(@Body CancelOrderRequestBean cancelOrderRequestBean);

    /**
     * ??????????????????sdk?????????
     *
     * @return
     */
    @POST(NetApiConstants.GET_USER_PAY_ORDER)
    Call<PayOrderResponseBean> getUserPayOrder(@Header("token") String token, @Body PayOrderRequestBean payOrderRequestBean);

    /**
     * ???????????????????????????id
     *
     * @return
     */
    @GET(NetApiConstants.GET_ADDRESS_BY_LL)
    Call<GetUserCouponListResponseBean> getCityIdByLl(@Query("lng") String lng, @Query("lng") String lat);

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_PICKUP_POINTLIST_BY_CITY)
    Call<GetUserCouponListResponseBean> getPickUpPointListByCity(@Query("userId") String userId);

    /**
     * ????????????
     *
     * @return
     */
    @POST(NetApiConstants.POST_DELETE_VIDEO)
    Call<DeleteVideoResponseBean> deleteVideo(@Body DeleteVideoRequestBean deleteVideoRequestBean);

    /**
     * ??????????????????????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_SWITCH)
    Call<SwitchResponseBean> getSwitch();

    /**
     * ????????????
     *
     * @return
     */
    @GET(NetApiConstants.GET_FIND_USER)
    Call<FindUserListBean> findUser(@Query("userNameOrIdOrPhone") String key);

}
