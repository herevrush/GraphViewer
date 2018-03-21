import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.logging.Logger;

public class ViewerMain {
    private final static Logger LOGGER = Logger.getLogger(ViewerMain.class.getName());

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Display display = new Display();
        final Shell shell = new Shell(display);
        org.eclipse.swt.layout.GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 1;
        shell.setLayout(gLayout);
        final Composite btnComposite = new Composite(shell, SWT.NONE);
        btnComposite.setLayout(new GridLayout(8,true));
        Button loadNodesBtn = new Button(btnComposite, SWT.PUSH);
        loadNodesBtn.setText("Load Nodes");
        Monitor primary = display.getPrimaryMonitor();

        /** get the size of the screen */
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();


        /** get the size of the window */
        Rectangle rect = shell.getBounds();

        /** calculate the centre */
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        /** set the new location */
        shell.setLocation(x, y);

        shell.setSize( bounds.width-100, bounds.height-100);
        shell.open();
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                event.doit = false;
                System.out.println(" Closing shell" + shell);
            }
        });
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }
}
