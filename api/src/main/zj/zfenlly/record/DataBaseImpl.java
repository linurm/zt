package zj.zfenlly.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.daodb.DaoMaster;
import zj.zfenlly.daodb.DaoSession;
import zj.zfenlly.daodb.MSC;
import zj.zfenlly.daodb.MSCDao;
import zj.zfenlly.daodb.WB;
import zj.zfenlly.daodb.WB2;
import zj.zfenlly.daodb.WB2Dao;
import zj.zfenlly.daodb.WBDao;


/**
 * Created by Administrator on 2016/7/18.
 */
public class DataBaseImpl {

    public static final boolean ENCRYPTED = false;


    static final String DATABASENAME = "mw.db";
    static final String DATABASEPATH = "xxx";

    static final String DATABASE_PATH_NAME = Environment.getExternalStorageDirectory() + "/"
            + DATABASEPATH + "/" + DATABASENAME;

    public static class MSCDataBaseOp {


        public static void insert(Context mContext, MSC mMsc) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            mscDao.insert(mMsc);
        }

        public static void delete(Context mContext, MSC mMSC) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            mscDao.deleteByKey(mMSC.getId());
        }

        public static MSC getCurrMSC(Context mContext) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            QueryBuilder<MSC> qb = mscDao.queryBuilder().orderAsc(MSCDao.Properties.Date);
            MSC mMSC = null;
            if (qb.list().size() >= 1)
                mMSC = qb.list().get(qb.list().size() - 1);
            return mMSC;
        }

        public static MSC getMSC(Context mContext, int id) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;
            if (id < 0) return null;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            QueryBuilder<MSC> qb = mscDao.queryBuilder().orderAsc(MSCDao.Properties.Date);
            MSC mMSC = null;
            if (qb.list().size() >= 1) {
                //mWB = qb.list().get(id);
                int len = qb.list().size();
                mMSC = qb.list().get(len - id - 1);
            }
            return mMSC;
        }

        public static List<MSC> getListMSC(Context mContext) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            QueryBuilder<MSC> qb = mscDao.queryBuilder().orderAsc(MSCDao.Properties.Date);
//            MSC mMSC = null;
//            if (qb.list().size() >= 1)
//                mMSC = qb.list().get(qb.list().size() - 1);

            List<MSC> goods = new ArrayList<MSC>();
            List<MSC> tmpgoods = mscDao.queryBuilder().orderAsc(MSCDao.Properties.Date).list();
            int len = tmpgoods.size();
            for (int i = len - 1; i >= 0; i--) {
                goods.add(tmpgoods.get(i));
            }
            return goods;
        }

    }

    public static class WBDataBaseOp {
        public static void insert(Context mContext, WB mWb) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            WBDao wbDao;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            wbDao.insert(mWb);
        }

        public static void delete(Context mContext, WB mWB) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            WBDao wbDao;

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();

            wbDao.deleteByKey(mWB.getId());
        }

        public static WB getCurrWB(Context mContext) {
            SQLiteDatabase db;
            WBDao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);
            ;
            WB mWB = null;
            if (qb.list().size() >= 1)
                mWB = qb.list().get(qb.list().size() - 1);
            return mWB;
        }

        public static WB getWB(Context mContext, int id) {
            SQLiteDatabase db;
            WBDao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            if (id < 0) return null;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);
            ;
            WB mWB = null;
            if (qb.list().size() >= 1) {
                //mWB = qb.list().get(id);
                int len = qb.list().size();
                mWB = qb.list().get(len - id - 1);
            }
            return mWB;
        }

        public static List<WB> getListWB(Context mContext) {
            SQLiteDatabase db;
            WBDao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            //QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);

            List<WB> goods = new ArrayList<WB>();
            List<WB> tmpgoods = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date).list();
            int len = tmpgoods.size();
            for (int i = len - 1; i >= 0; i--) {
                goods.add(tmpgoods.get(i));
            }
            return goods;

        }
    }

    public static class WB2DataBaseOp {
        public static void insert(Context mContext, WB2 mWb) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            WB2Dao wbDao;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWB2Dao();
            Log.e("TAG", "insert " + mWb.getId());
            wbDao.insert(mWb);
        }

        public static void delete(Context mContext, WB2 mWB) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            WB2Dao wbDao;

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWB2Dao();
            Log.e("TAG", "delete " + mWB.getId());
            wbDao.deleteByKey(mWB.getId());
        }

        public static WB2 getCurrWB(Context mContext) {
            SQLiteDatabase db;
            WB2Dao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWB2Dao();
            QueryBuilder<WB2> qb = wbDao.queryBuilder().orderAsc(WB2Dao.Properties.Date);
            ;
            WB2 mWB = null;
            if (qb.list().size() >= 1)
                mWB = qb.list().get(qb.list().size() - 1);
            return mWB;
        }

        public static WB2 getWB(Context mContext, int id) {
            SQLiteDatabase db;
            WB2Dao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            if (id < 0) return null;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWB2Dao();
            QueryBuilder<WB2> qb = wbDao.queryBuilder().orderAsc(WB2Dao.Properties.Date);
            ;
            WB2 mWB = null;
            //List<WB2> goods = new ArrayList<WB2>();
            if (qb.list().size() >= 1) {
                //mWB = qb.list().get(id);
                int len = qb.list().size();
                mWB = qb.list().get(len - id - 1);
            }
            return mWB;
        }

        public static List<WB2> getListWB(Context mContext) {
            SQLiteDatabase db;
            WB2Dao wbDao;
            DaoMaster daoMaster;
            DaoSession daoSession;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH_NAME, null);
            db = helper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWB2Dao();
            //QueryBuilder<WB2> qb = wbDao.queryBuilder().orderAsc(WB2Dao.Properties.Date);

            List<WB2> goods = new ArrayList<WB2>();
            List<WB2> tmpgoods = wbDao.queryBuilder().orderAsc(WB2Dao.Properties.Date).list();
            int len = tmpgoods.size();
            for (int i = len - 1; i >= 0; i--) {
                goods.add(tmpgoods.get(i));
            }
            return goods;
        }
    }
}
