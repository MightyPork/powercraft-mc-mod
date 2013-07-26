package powercraft.api.gres;


import powercraft.apiOld.PC_RectI;
import powercraft.apiOld.PC_Vec2I;


public class PC_GresLabel extends PC_GresComponent {

	public PC_GresLabel(String text) {

		setText(text);
	}


	@Override
	protected PC_Vec2I calculateMinSize() {

		return new PC_Vec2I(fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT);
	}


	@Override
	protected PC_Vec2I calculateMaxSize() {

		return new PC_Vec2I(-1, -1);
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return new PC_Vec2I(-1, -1);
	}


	@Override
	protected void paint(PC_RectI scissor, float timeStamp) {

		drawString(text, 0, 0, rect.width, rect.height, alignH, alignV, false);
	}

}
