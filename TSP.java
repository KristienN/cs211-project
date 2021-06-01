import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TSP {

    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }
}

class Window extends JFrame implements ActionListener {

    // All the window components
    JButton compute = new JButton();
    JLabel label = new JLabel();
    JPanel side = new JPanel();
    JTextArea input = new JTextArea();
    JTextArea output = new JTextArea();
    JScrollPane sp, op;
    Main main;

    // Route Data object
    RouteData rd;

    public String inputText, delayText;
    public int delay = 0;

    Window() {
        // To create Frame
        this.setSize(800, 600);
        this.setTitle("Travelling Salesperson | Kildare");
        this.setResizable(false);
        // this.setLocation(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Panel
        main = new Main();

        // Input Area
        input.setPreferredSize(new Dimension(200, 250));
        input.setToolTipText("Enter Data Here");
        sp = new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Output Area
        output.setPreferredSize(new Dimension(200, 50));
        output.setText("Output Path");
        output.setEditable(false);

        op = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Compute Button
        compute.setText("Compute");
        compute.setBackground(Color.green);
        compute.setFont(new Font("Consolas", Font.BOLD, 18));
        compute.setSize(180, 180);
        compute.setFocusable(false);
        compute.addActionListener(this);

        // Label for Angry Mins
        delayText = "Angry Mins: 0";
        label.setText(delayText);
        label.setFont(new Font("Sans-serif", 15, 15));

        // Side Panel
        side.setBackground(Color.gray);
        side.setPreferredSize(new Dimension(250, 100));
        side.add(sp);
        side.add(compute);
        side.add(op);
        side.add(label);

        this.add(main, BorderLayout.WEST);
        this.add(side, BorderLayout.EAST);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // When Button is clicked
        if (e.getSource() == compute) {
            // Get text from text area
            inputText = input.getText();

            // Create route data object
            rd = new RouteData(inputText);

            // After calculations set the delay
            label.setText("Angry Mins: " + rd.delay);

            // Set the solution of orders
            output.setText(rd.solution);

            // Draw the points and lines
            main.setData(rd.nearestN);
            main.update();
        }
    }

    public String getData() {
        return inputText;
    }

    public JButton getCompute() {
        return compute;
    }

}

class Main extends JPanel {

    // The main panel
    public Node[] n;
    public int r, xMax, yMax, yMin, xMin;
    public String answer, delay;
    // private Image map;

    Main() {
        this.setPreferredSize(new Dimension(550, 100));
        this.setBackground(new Color(0, 50, 0));

        // map = ImageIO.read(new File("s_map.png"));

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        r = 6; // radius of points

        // g.drawImage(map, 0, 0, this);

        // // Sample Data
        // String data = "1,38 Parsons Hall Maynooth ,4,53.37521,-6.6103";
        // data += "\n2,34 Silken Vale Maynooth ,6,53.37626,-6.59308";
        // data += "\n3,156 Glendale Leixlip ,18,53.37077,-6.48279";
        // data += "\n4,33 The Paddocks Oldtown Mill Celbridge ,8,53.3473,-6.55057";
        // data += "\n5,902 Lady Castle K Club Straffan ,11,53.31159,-6.60538";
        // data += "\n6,9 The Park Louisa Valley Leixlip ,3,53.36115,-6.48907";
        // data += "\n7,509 Riverforest Leixlip ,10,53.37402,-6.49363";
        // data += "\n8,16 Priory Chase St.Raphaels Manor
        // Celbridge,7,53.33886,-6.55468";
        // data += "\n9,13 Abbey Park Court Clane,13,53.2908,-6.67746";
        // data += "\n10,117 Royal Meadows Kilcock ,12,53.39459,-6.66995";
        // data += "\n11,7 Riverlawn Abbeyfarm Celbridge ,3,53.33239,-6.55163";
        // data += "\n12,10 Fair Green Court Kilccock ,7,53.39847,-6.66787";
        // data += "\n13,11 The Lodge Abbeylands Clane,12,53.29128,-6.67836";
        // data += "\n14,628 Riverforest Leixlip ,5,53.37416,-6.49731";
        // data += "\n15,12 Castlevillage Avenue Celbridge ,8,53.35298,-6.54921";
        // data += "\n16,116 Connaught Street Kilcock ,4,53.39839,-6.66767";
        // data += "\n17,44 Rinawade Avenue Leixlip ,20,53.36141,-6.51834";
        // data += "\n18,35 Beech Park Wood Beech Park Leixlip ,14,53.36287,-6.52468";
        // data += "\n19,96 Priory Lodge St. Raphael's Manor
        // Celbridge,2,53.33835,-6.53984";
        // data += "\n20,33 Leinster Wood Carton Demesne Maynooth ,7,53.39351,-6.5542";
        // data += "\n21,6 Glen Easton View Leixlip ,15,53.36883,-6.51468";
        // data += "\n22,40 Oaklawn West. Leixlip ,8,53.36833,-6.50589";
        // data += "\n23,169 Glendale Leixlip ,24,53.37043,-6.48193";
        // data += "\n24,14 The Rise Louisa Valley Leixlip ,15,53.36115,-6.48907";
        // data += "\n25,28 The Lawn Moyglare Abbey Maynooth ,7,53.38895,-6.60579";
        // data += "\n26,43 The Woodlands Castletown Celbridge ,12,53.34678,-6.53415";
        // data += "\n27,14 Rye River Crescent Dun Carraig Leixlip,8,53.36518,-6.48913";
        // data += "\n28,32 The View St.Wolstan Abbey Celbridge ,10,53.33751,-6.53173";
        // data += "\n29,20 Habourview The Glenroyal Centre
        // Maynooth,9,53.37954,-6.58793";
        // data += "\n30,416A Ballyoulster Celbridge ,5,53.34133,-6.51856";
        // data += "\n31,10 Brookfield Avenue Maynooth ,8,53.36976,-6.59828";
        // data += "\n32,15 Willow Rise Primrose Gate Celbridge ,19,53.33591,-6.53566";
        // data += "\n33,3 Lyreen Park Maynooth ,26,53.38579,-6.58673";
        // data += "\n34,2 Beaufield Drive Maynooth ,10,53.37414,-6.60028";
        // data += "\n35,28 The Avenue Castletown Celbridge ,18,53.34514,-6.53615";
        // data += "\n36,4 Abbey Park Grove Clane ,14,53.29206,-6.67685";
        // data += "\n37,78 Crodaun Forest Park Celbridge ,15,53.35401,-6.54603";
        // data += "\n38,1 Kyldar House Manor Mills Maynooth ,29,53.38122,-6.59226";
        // data += "\n39,1002 Avondale Leixlip ,22,53.36869,-6.48314";
        // data += "\n40,18 College Green Maynooth ,5,53.37247,-6.60044";

        // RouteData rd = new RouteData(data);

        if (n != null) {

            // Draw if the input is not empty

            g.setColor(Color.green);
            g.fillOval(n[0].x, n[0].y, r, r);

            for (int i = 1; i < n.length; i++) {

                g.setColor(Color.red);
                g.fillOval(n[i].x, n[i].y, r, r);

                g.setColor(Color.white);
                g.drawLine(n[i - 1].x, n[i - 1].y, n[i].x, n[i].y);
            }

        }

    }

    protected void update() {
        repaint();

        // Update
    }

    // Getter and Setter Methods

    public void setData(Node[] n) {
        this.n = n;
    }

    public void setAns(String text) {
        this.answer = text;
    }

    public void setDelay(String text) {
        this.delay = text;
    }

    public String getAns() {
        return this.answer;
    }

    public String getDelay() {
        return this.delay;
    }

}

class RouteData {

    // Route Data Object

    public Node[] orders; // To store initial orders

    public Node[] nearestN; // to store the nearest neighbour orders
    public Node start; // to store the apache pizza starting location

    public double delay; // to store the delay
    public String solution; // to store the solution string

    RouteData(String data) {

        if (!data.equals("")) {

            // Compute if the data string is not empty

            this.orders = convertString(data);

            this.start = new Node(0, 0, 53.38197, -6.59274);

            this.nearestN = nearestNeighbour(this.orders, this.start);
            this.delay = angryMins(nearestN);

            int xMax = maxValueX(nearestN);
            int yMax = maxValueY(nearestN);
            int xMin = minValueX(nearestN);
            int yMin = minValueY(nearestN);

            // Scaling the points
            for (int i = 0; i < nearestN.length; i++) {
                nearestN[i].x = (int) scalePoint(500, xMax, xMin, nearestN[i].x);
                nearestN[i].y = (int) scalePoint(500, yMax, yMin, nearestN[i].y);
            }

            this.solution = getPath(nearestN);
        }

    }

    public Node[] convertString(String data) {
        String[] line = data.split("\n");
        Node[] order = new Node[line.length];

        for (int i = 0; i < line.length; i++) {
            String[] cut = line[i].split(",");

            int index = Integer.parseInt(cut[0]);
            int mins = Integer.parseInt(cut[2]);

            double lat = Double.parseDouble(cut[3]);
            double lon = Double.parseDouble(cut[4]);

            order[i] = new Node(index, mins, lat, lon);
        }

        return order;
    }

    public Node[] nearestNeighbour(Node[] orders, Node start) {

        Node[] nn = new Node[orders.length + 1];
        nn[0] = start;
        int count = 0;
        int j = 1;
        int index = 0;
        int next = 0;
        while (count < orders.length) {

            double min = Double.MAX_VALUE;

            for (int i = 0; i < orders.length; i++) {
                if (index != i) {
                    double distance = getDistance(orders[index].lat, orders[i].lat, orders[index].lon, orders[i].lon);
                    int order = orders[i].order;
                    if (distance < min && check(order, nn)) {
                        next = i;
                        min = distance;
                    }
                }
            }
            nn[j] = orders[next];
            index = next;
            j++;
            count++;

        }

        return nn;
    }

    public double getDistance(double lat1, double lat2, double long1, double long2) {

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);

        double abdiff = long1 > long2 ? long1 - long2 : long2 - long1;

        double sigma = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(abdiff);
        sigma = Math.acos(sigma);
        double r = 6371.0;
        double d = r * sigma;

        return d;
    }

    public boolean check(int index, Node[] nn) {
        boolean check = true;
        for (int i = 0; i < nn.length; i++) {
            if (nn[i] != null) {
                if (nn[i].order == index) {
                    check = false;
                }
            }
        }

        return check;
    }

    public double angryMins(Node[] n) {

        double time = 0;
        double delay = 0;

        for (int i = 0; i < n.length - 1; i++) {
            double distance = getDistance(n[i].lat, n[i + 1].lat, n[i].lon, n[i + 1].lon);
            double travel_time = timeElapsed(distance);

            time = time + travel_time;
            double total_waitTime = n[i + 1].mins + time;

            if (total_waitTime > 30) {
                delay = delay + (total_waitTime - 30);
            }

            // System.out.println(total_waitTime);
        }

        return delay;
    }

    public double timeElapsed(double dist) {

        double speed = 60.0;
        double hours = dist / speed;
        double mins = hours * 60;

        return mins;
    }

    public double scalePoint(int size, int max, int min, int point) {
        double result = 0;
        double x = (point - min);

        double y = x / (max - min);
        result = size * y;

        // System.out.println(result);
        return result + 10;
    }

    public int minValueX(Node[] arr) {
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].x < min) {
                min = arr[i].x;
            }
        }
        return min;
    }

    public int minValueY(Node[] arr) {
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].y < min) {
                min = arr[i].y;
            }
        }
        return min;
    }

    public int maxValueY(Node[] arr) {
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].y > max) {
                max = arr[i].y;
            }
        }
        return max;
    }

    public int maxValueX(Node[] arr) {

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].x > max) {
                max = arr[i].x;
            }
        }
        return max;

    }

    public String getPath(Node[] n) {
        String answer = "";

        for (int i = 1; i < n.length; i++) {
            answer += n[i].order + ", ";

            if (i % 12 == 0) {
                answer += "\n";
            }
        }

        return answer;
    }

}

// Single Node Object with the point information
class Node {
    public int order, mins, x, y;
    public double lat, lon;

    Node(int order, int mins, double lat, double lon) {
        this.order = order;
        this.mins = mins;
        this.lat = lat;
        this.lon = lon;

        // Conversion
        this.x = (int) ((lat - 53) * 1000);
        this.y = (int) ((6 + lon) * 1000) * (-1);

    }

}