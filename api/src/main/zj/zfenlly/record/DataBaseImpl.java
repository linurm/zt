package zj.zfenlly.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.zfenlly.db.DaoMaster;
import com.zfenlly.db.DaoSession;
import com.zfenlly.db.MSC;
import com.zfenlly.db.MSCDao;
import com.zfenlly.db.WB;
import com.zfenlly.db.WBDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2016/7/18.
 */
public class DataBaseImpl {
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
            if (qb.list().size() >= 1)
                mMSC = qb.list().get(id);
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
            return qb.list();
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
            QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);;
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
            QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);;
            WB mWB = null;
            if (qb.list().size() >= 1)
                mWB = qb.list().get(id);
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
            QueryBuilder<WB> qb = wbDao.queryBuilder().orderAsc(WBDao.Properties.Date);
            return qb.list();
        }
    }
}
