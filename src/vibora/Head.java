package vibora;


import com.golden.gamedev.object.sprite.AdvanceSprite;
import java.util.*;

public class Head extends AdvanceSprite{
    ArrayList<Tail> cola;
    
    public Head(){
        super();
        cola = new ArrayList<Tail>();       
    }
    
    public boolean moveVertical(long elapsedTime, double lapso){
        update(elapsedTime);
        moveY(lapso);
        return true;
    }

    public boolean moveHorizontal(long elapsedTime, double lapso){
        update(elapsedTime);
        moveX(lapso);
        return true;
    }
}
