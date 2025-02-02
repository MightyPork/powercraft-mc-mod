package powercraft.logic;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.registry.PC_LangRegistry.LangEntry;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_AddSplashes;
import powercraft.launcher.loader.PC_ClientModule.PC_InitLanguage;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;

@PC_ClientModule
public class PClo_AppClient extends PClo_App {
	
	@PC_InitLanguage
	public List<LangEntry> initLanguage(List<LangEntry> lang) {
		lang.add(new LangEntry("pc.pulsar.clickMsg", "Period %s ticks (%s s)"));
		lang.add(new LangEntry("pc.pulsar.clickMsgTime", "Period %s ticks (%s s), remains %s"));
		lang.add(new LangEntry("pc.gate.not.desc", "negates input"));
		lang.add(new LangEntry("pc.gate.and.desc", "all inputs on"));
		lang.add(new LangEntry("pc.gate.nand.desc", "some inputs off"));
		lang.add(new LangEntry("pc.gate.or.desc", "at least one input on"));
		lang.add(new LangEntry("pc.gate.nor.desc", "all inputs off"));
		lang.add(new LangEntry("pc.gate.xor.desc", "inputs different"));
		lang.add(new LangEntry("pc.gate.xnor.desc", "inputs equal"));
		lang.add(new LangEntry("pc.flipflop.D.desc", "latch memory"));
		lang.add(new LangEntry("pc.flipflop.RS.desc", "set/reset memory"));
		lang.add(new LangEntry("pc.flipflop.T.desc", "divides signal by 2"));
		lang.add(new LangEntry("pc.flipflop.random.desc", "changes state randomly on pulse"));
		lang.add(new LangEntry("pc.delayer.buffer.desc", "slows down signal"));
		lang.add(new LangEntry("pc.delayer.slowRepeater.desc", "makes pulses longer"));
		lang.add(new LangEntry("pc.special.day.desc", "on during day"));
		lang.add(new LangEntry("pc.special.night.desc", "on during night"));
		lang.add(new LangEntry("pc.special.rain.desc", "on during rain"));
		lang.add(new LangEntry("pc.special.chestEmpty.desc", "on if nearby container is empty"));
		lang.add(new LangEntry("pc.special.chestFull.desc", "on if nearby container is full"));
		lang.add(new LangEntry("pc.special.special.desc", "spawner & pulsar control"));
		lang.add(new LangEntry("pc.repeater.crossing.desc", "lets two wires intersect"));
		lang.add(new LangEntry("pc.repeater.splitter.desc", "splits signal"));
		lang.add(new LangEntry("pc.repeater.repeaterStraight.desc", "simple 1-tick repeater"));
		lang.add(new LangEntry("pc.repeater.repeaterCorner.desc", "simple 1-tick corner repeater"));
		lang.add(new LangEntry("pc.repeater.repeaterStraightInstant.desc", "instant repeater"));
		lang.add(new LangEntry("pc.repeater.repeaterCornerInstant.desc", "instant corner repeater"));
		lang.add(new LangEntry("pc.gui.pulsar.silent", "Silent"));
		lang.add(new LangEntry("pc.gui.pulsar.paused", "Pause"));
		lang.add(new LangEntry("pc.gui.pulsar.delay", "Delay (sec)"));
		lang.add(new LangEntry("pc.gui.pulsar.hold", "Hold time (sec)"));
		lang.add(new LangEntry("pc.gui.pulsar.ticks", "ticks"));
		lang.add(new LangEntry("pc.gui.pulsar.errDelay", "Bad delay time!"));
		lang.add(new LangEntry("pc.gui.pulsar.errHold", "Bad hold time!"));
		lang.add(new LangEntry("pc.gui.delayer.delay", "Delay (sec)"));
		lang.add(new LangEntry("pc.gui.pulsar.errintputzero", "Bad delay time!"));
		lang.add(new LangEntry("pc.gui.delayer.errnoinput", "No Input!"));
		lang.add(new LangEntry("pc.gui.special.chestEmpty.name", "Empty Chest"));
		lang.add(new LangEntry("pc.gui.special.chestEmpty.inv", "No item of kind"));
		lang.add(new LangEntry("pc.gui.special.chestFull.name", "Full Chest"));
		lang.add(new LangEntry("pc.gui.special.chestFull.inv", "Space for"));
		return lang;
	}

	@PC_AddSplashes
    public List<String> addSplashes(List<String> list)
    {
        list.add("Adjustable clock pulse!");
        return list;
    }

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Delayer", PClo_GuiDelayer.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Pulsar", PClo_GuiPulsar.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Special", PClo_GuiSpecial.class));
		return guis;
	}
	
}
