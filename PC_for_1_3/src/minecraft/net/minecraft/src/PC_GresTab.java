package net.minecraft.src;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.opengl.GL11;

public class PC_GresTab extends PC_GresWidget {

	private Map<PC_GresWidget, PC_GresWidget> tab = new HashMap<PC_GresWidget, PC_GresWidget>();
	private int tabHight;
	private int tabIndex=0;
	private boolean sideButtons = false;
	private PC_GresWidget mouseOver = null;
	private boolean isClicked = false;
	private int button=0;
	private boolean noright = true;
	
	public PC_GresTab(){
	}
	
	public PC_GresWidget makeTabVisible(PC_GresWidget widget){
		for(PC_GresWidget w:childs){
			w.visible = w==widget;
		}
		return this;
	}
	
	@Override
	public PC_GresWidget add(PC_GresWidget w){
		addTab(w, new PC_GresLabel(w.getText()));
		return this;
	}
	
	public PC_GresTab addTab(PC_GresWidget w, PC_GresWidget t){
		super.add(w);
		tab.put(w, t);
		makeTabVisible(w);
		return this;
	}
	
	@Override
	public PC_CoordI calcSize() {
		PC_CoordI s = new PC_CoordI();
		PC_CoordI ws;
		
		sideButtons = false;
		tabHight = 0;
		PC_CoordI size;
		int xSize=0;
		if(tab!=null){
			for(PC_GresWidget w:tab.values()){
				w.setSize(size = w.calcSize());
				xSize += size.x + 6 + 2;
				if(tabHight<size.y+6){
					tabHight = size.y+6;
				}
			}
		}
		if(xSize-2>this.size.x)
			sideButtons = true;
		for(PC_GresWidget w:childs){
			ws = w.calcSize();
			if(s.x<ws.x+6)
				s.x=ws.x+6;
			if(s.y<ws.y+tabHight+6)
				s.y=ws.y+tabHight+6;
		}
		if(s.x<minSize.x)
			s.x = minSize.x;
		if(s.y<minSize.y)
			s.y = minSize.y;
		return s;
	}

	@Override
	public void calcChildPositions() {
		size.setTo(calcSize());
		for(PC_GresWidget w:tab.values()){
			w.setPosition(0, 0);
		}
		
		for(PC_GresWidget w:childs)
			w.setPosition(3, tabHight+3);
	}
	
	private int renderTab(PC_CoordI posOffset, PC_RectI scissorOld,
			double scale, int x, PC_GresWidget w){
		int width = w.getSize().x+6;
		
		if(x+width>size.x-(sideButtons?12:0))
			return -1;
		
		drawHorizontalLine(posOffset.x+pos.x+x, posOffset.x+pos.x+x+width-2, posOffset.y+pos.y, 0xFFFFFFFF);
		drawVerticalLine(posOffset.x+pos.x+x, posOffset.y+pos.y, posOffset.y+pos.y+tabHight, 0xFFFFFFFF);
		drawVerticalLine(posOffset.x+pos.x+x+width-1, posOffset.y+pos.y, posOffset.y+pos.y+tabHight, 0xFF373737);
		drawHorizontalLine(posOffset.x+pos.x+x+width-1, posOffset.x+pos.x+x+width-1, posOffset.y+pos.y, 0xFF8B8B8B);
		
		if(w==mouseOver && isMouseOver){
			drawRect(posOffset.x+pos.x+x+1, posOffset.y+pos.y+1, posOffset.x+pos.x+x+width-1, posOffset.y+pos.y+tabHight, 0x800000FF);
		}
		
		w.render(posOffset.offset(pos.x+x+3, pos.y+3), scissorOld, scale);
		return width;
	}
	
	private void renderButton(PC_CoordI posOffset, boolean left){
		int state;
		if((!left && tabIndex==0) || (left && noright)){
			state = 0;
		}else if (button==(left?1:2) && isMouseOver) {
			if(isClicked)
				state = 3; // enabled and clicked
			else
				state = 2;
		} else {
			state = 1; // enabled and not hover
		}
		
		posOffset = posOffset.offset((left?0:size.x-10), tabHight/2-6);
		
		renderTextureSliced(posOffset, mod_PCcore.getImgDir() + "gres/button.png", new PC_CoordI(10, 12), new PC_CoordI(0, state * 50), new PC_CoordI(256, 50));

		// and here goes the image
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, PC_Utils.mc().renderEngine.getTexture(mod_PCcore.getImgDir() + "gres/widgets.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		drawTexturedModalRect(pos.x + posOffset.x + 3, pos.y + posOffset.y + 3, left?68:72,
				0, 4, 6);

		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	protected PC_RectI render(PC_CoordI posOffset, PC_RectI scissorOld,
			double scale) {
		
		int i=tabIndex;
		int x=sideButtons?12:0;
		int width = 0;
		int xFree = 0;
		int xFreeWidth = 0;
		boolean nnoright = true;
		for(PC_GresWidget c:childs){
			if(i==0){
				width = renderTab(posOffset, scissorOld, scale, x, tab.get(c));
				if(width == -1){
					nnoright=false;
					break;
				}
				if(c.isVisible()){
					xFree = x;
					xFreeWidth = width;
				}
				x += width + 2;
			}else{
				i--;
			}
		}
		
		noright = nnoright;
		
		if(sideButtons){
			renderButton(posOffset, true);
			renderButton(posOffset, false);
		}
		
		drawHorizontalLine(posOffset.x+pos.x, posOffset.x+pos.x+xFree, posOffset.y+pos.y+tabHight, 0xFFFFFFFF);
		drawHorizontalLine(posOffset.x+pos.x+xFree+xFreeWidth, posOffset.x+pos.x+size.x-2, posOffset.y+pos.y+tabHight, 0xFFFFFFFF);
		drawHorizontalLine(posOffset.x+pos.x+1, posOffset.x+pos.x+size.x-1, posOffset.y+pos.y+size.y-1, 0xFF373737);
		
		drawVerticalLine(posOffset.x+pos.x, posOffset.y+pos.y+tabHight, posOffset.y+pos.y+size.y-1, 0xFFFFFFFF);
		drawVerticalLine(posOffset.x+pos.x+size.x-1, posOffset.y+pos.y+tabHight, posOffset.y+pos.y+size.y-1, 0xFF373737);
		
		drawHorizontalLine(posOffset.x+pos.x+size.x-1, posOffset.x+pos.x+size.x-1, posOffset.y+pos.y+tabHight, 0xFF8B8B8B);
		drawHorizontalLine(posOffset.x+pos.x, posOffset.x+pos.x, posOffset.y+pos.y+size.y-1, 0xFF8B8B8B);
		
		drawHorizontalLine(posOffset.x+pos.x+xFree+xFreeWidth-1, posOffset.x+pos.x+xFree+xFreeWidth-1, posOffset.y+pos.y+tabHight, 0xFF8B8B8B);
		
		return null;
	}
	
	public PC_GresWidget getTab(int xTab){
		int i=tabIndex;
		int x=sideButtons?12:0;
		int width = 0;
		for(PC_GresWidget c:childs){
			if(i==0){
				width = tab.get(c).getSize().x+6;
				if(x+width>size.x-(sideButtons?12:0)){
					break;
				}
				if(xTab < x)
					return null;
				if(xTab >= x && xTab < x + width){
					return tab.get(c);
				}
				x += width + 2;
			}else{
				i--;
			}
		}
		return null;
	}
	
	@Override
	public MouseOver mouseOver(PC_CoordI mousePos) {
		if(mousePos.y<0||mousePos.x<0||mousePos.x>size.x){
			mouseOver = null;
			isClicked = false;
			button = 0;
			return MouseOver.NON;
		}
		if(mousePos.y<tabHight){
			PC_GresWidget nMouseOver = getTab(mousePos.x);
			if(nMouseOver != mouseOver){
				mouseOver = nMouseOver;
				isClicked = false;
			}
			button = 0;
			if(mouseOver!=null)
				return MouseOver.THIS;
			if(sideButtons && mousePos.y<tabHight/2+6 && mousePos.y>tabHight/2-6){
				if(mousePos.x<10){
					button = 1;
				}else if(mousePos.x>size.x-10){
					button = 2;
				}
			}
			if(button==0)
				isClicked = false;
			return MouseOver.THIS;
		}
		mouseOver = null;
		isClicked = false;
		button=0;
		return MouseOver.CHILD;
	}

	@Override
	public boolean mouseClick(PC_CoordI mousePos, int key) {
		if(mousePos.y<tabHight){
			if(key!=-1){
				mouseOver = getTab(mousePos.x);
				isClicked = true;
				button = 0;
				if(mouseOver!=null)
					return true;
				if(sideButtons && mousePos.y<tabHight/2+6 && mousePos.y>tabHight/2-6){
					if(mousePos.x<10){
						button = 1;
					}else if(mousePos.x>size.x-10){
						button = 2;
					}
				}
				if(button!=0)
					return true;
				isClicked = false;
				return true;
			}else if(isClicked){
				isClicked = false;
				if(mouseOver!=null){
					Set<Entry<PC_GresWidget, PC_GresWidget>> se = tab.entrySet();
					for(Entry<PC_GresWidget, PC_GresWidget> e:se){
						if(e.getValue()==mouseOver){
							makeTabVisible(e.getKey());
							return true;
						}
					}
					return false;
				}else{
					if(button==1){
						if(!noright)
							tabIndex++;
					}else if(button==2){
						tabIndex--;
						if(tabIndex<0)
							tabIndex=0;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void mouseMove(PC_CoordI mousePos) {
		mouseOver(mousePos);
	}

	@Override
	public void mouseWheel(int i) {
		
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void addedToWidget() {
	}

}
