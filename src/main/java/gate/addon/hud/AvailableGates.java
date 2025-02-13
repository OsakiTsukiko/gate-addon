package gate.addon.hud;

import com.fasterxml.jackson.core.type.TypeReference;
import gate.addon.GateAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

class ResponseItem {
    public String label;
    public boolean ready;

    ResponseItem(String label, boolean ready) {
        this.label = label;
        this.ready = ready;
    }
}

public class AvailableGates extends HudElement {
    public static final HudElementInfo<AvailableGates> INFO = new HudElementInfo<>(GateAddon.HUD_GROUP, "Available Gates", "Show Available Gates.", AvailableGates::new);
    private int tick_nr = 0;
    List<ResponseItem> resp_list = List.of(new ResponseItem("Gate 1", true), new ResponseItem("Gate 2", false));

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<String> instance = sgGeneral.add(new StringSetting.Builder()
        .name("instance-url")
        .description("The url of the instance.")
        .defaultValue("localhost:8080")
        .wide()
        .build()
    );

    public final Setting<String> token = sgGeneral.add(new StringSetting.Builder()
        .name("token")
        .description("The token for the instance.")
        .defaultValue("...")
        .wide()
        .build()
    );

    public final Setting<Boolean> is_active = sgGeneral.add(new BoolSetting.Builder()
        .name("ping-server")
        .description("Ping server constantly!")
        .defaultValue(false)
        .build()
    );

    public AvailableGates() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (tick_nr % 20 * 5 == 0) {
            if (is_active.get()) {
                String str_url = instance.get();

                Http.Request req = Http.get("http://" + str_url + "/ls");
                req.bearer(token.get());

                Type listType = new TypeReference<List<ResponseItem>>() {}.getType();

                // Send the request and get the response, passing the type for deserialization

                try {

                    HttpResponse<List<ResponseItem>> response = req.sendJsonResponse(listType);

                    // Process the response
                    this.resp_list = response.body();
                } catch (Exception e) {
                    this.is_active.set(false);
                    tick_nr += 1;
                    return;
                }
            }
        }
        tick_nr += 1;
    }

    @Override
    public void render(HudRenderer renderer) {
        List<ResponseItem> my_resp_list = this.resp_list;
        if (my_resp_list == null) my_resp_list = List.of(new ResponseItem("Gate 1", true), new ResponseItem("Gate 2", false));

        double max_width = 0;
        for (ResponseItem ri : my_resp_list) {
            double w = renderer.textWidth(ri.label + " " + ((ri.ready) ? "ON" : "OFF"), false);
            if (w > max_width) max_width = w;
        }

        setSize(max_width, my_resp_list.size() * renderer.textHeight(false));

        // Render background
        renderer.quad(x, y, getWidth(), getHeight(), Color.BLACK);

        int index = 0;
        for (ResponseItem ri : my_resp_list) {
            // Render text
            renderer.text(ri.label + " " + ((ri.ready) ? "ON" : "OFF"), x, y + renderer.textHeight(false) * index, Color.WHITE, false);
            index += 1;
        }
    }
}
