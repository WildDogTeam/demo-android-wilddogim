package filetype;

import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.wilddog.ApplicationTest;

import junit.framework.TestCase;

/**
 * Created by Administrator on 2016/12/14.
 */
public class FileTypeTest extends TestCase {

    public void testFileType(){
        /*D:\wildim\Wilddog-im-android\demo-android-wilddogim\src\androidTest\java\filetype\addbutton.png*/
     String fileType = FileType.getFileType("");
      //  String fileType = FileType.getFileType("D:\\wildim\\Wilddog-im-android\\demo-android-wilddogim\\src\\androidTest\\java\\filetype\\addbutton.png");
       System.out.println("fileType:"+fileType);

    }
}
