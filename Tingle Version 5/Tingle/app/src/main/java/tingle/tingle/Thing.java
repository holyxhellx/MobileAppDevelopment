package tingle.tingle;

public class Thing {
        //Data fields
        private String mWhat = null;
        private String mWhere = null;

        //Constructor
        public Thing(String what, String where) {
            mWhat = what;
            mWhere = where;
        }

        //Methods
        @Override
        public String toString() {
            return oneLine("Item: ","Location: ");
        }
        public String getWhat() {
            return mWhat;
        }
        public void setWhat(String what) {
            mWhat = what;
        }
        public String getWhere() {
            return mWhere;
        }
        public void setWhere(String where) {
            mWhere = where;
        }
        public String oneLine(String pre, String post) {
            return pre + mWhat + "\n"+ post + mWhere;
        }
}

