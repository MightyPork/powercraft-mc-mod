package weasel.compiler.v2.tokentree;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import weasel.compiler.WeaselCompilerException;
import weasel.compiler.WeaselToken;
import weasel.compiler.WeaselTokenType;

public class WeaselArrayInit {

	private List<Object> indexes = new ArrayList<Object>();
	
	public WeaselArrayInit(ListIterator<WeaselToken> iterator, int arrays) throws WeaselCompilerException {
		boolean canComeArray = arrays>1;
		while(true){
			WeaselToken token = iterator.next();
			if(token.tokenType==WeaselTokenType.CLOSEBLOCK)
				break;
			if(token.tokenType==WeaselTokenType.OPENBLOCK){
				if(canComeArray)
					throw new WeaselCompilerException(token.line, "Too many {");
				indexes.add(new WeaselArrayInit(iterator, arrays-1));
			}else{
				iterator.previous();
				indexes.add(WeaselTree.parse(iterator, WeaselTokenType.COMMA, WeaselTokenType.CLOSEBLOCK));
				iterator.previous();
				token = iterator.next();
				if(token.tokenType==WeaselTokenType.CLOSEBLOCK)
					break;
			}
		}
	}

}
