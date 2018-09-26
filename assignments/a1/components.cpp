#include <string>
#include "components.h"

Ball::Ball(double xPos, double yPos, GC gc, int ballSize, double ballSpeed): 
            xPosition(xPos), yPosition(yPos), gc(gc), ballSize(ballSize),
            xDirection(ballSpeed), yDirection(-ballSpeed),
            startingXPosition(xPos), startingYPosition(yPos) {}
virtual void Ball::paint(Display* display, Pixmap buffer) const {
    XFillArc( display, buffer, gc,
                xPosition - ballSize/2,
                yPosition - ballSize/2,
                ballSize, ballSize,
                0, 360*64);
}
double Ball::xPos() const {
    return xPosition;
}
double Ball::yPos() const {
    return yPosition;
}
double Ball::xDir() const {
    return xDirection;
}
double Ball::yDir() const {
    return yDirection;	
}
void Ball::updatePos() {
    xPosition += xDirection;
    yPosition += yDirection;
}
void Ball::invertXDir() {
    xDirection = -xDirection;
}
void Ball::invertYDir() {
    yDirection = -yDirection;
}
int Ball::size() const {
    return ballSize;
}
void Ball::reset() {
    xPosition = startingXPosition;
    yPosition = startingYPosition;
    xDirection = abs(xDirection);
    yDirection = yDirection > 0 ? -yDirection : yDirection;
}

/****************************************************************/

Block::Block(int x, int y, GC gc, int health = 1): x(x), y(y), gc(gc),
                                                                                        currentHealth(health), maxHealth(health) {}
virtual void Block::paint(Display* display, Pixmap buffer) const {
    XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
}
int xPos() const {
    return x;
}
int yPos() const {
    return y;
}
int width() const {
    return widthDimension;
}
int height() const {
    return heightDimension;
}
void onHit() {
    --currentHealth;
}
bool isDestroyed() const {
    return currentHealth == 0;
}
void reset() {
    currentHealth = maxHealth;
}

/**************************************************************/

Paddle::Paddle(int x, int y, GC gc): x(x), y(y), startingXPosition(x), startingYPosition(y), gc(gc) {}
virtual void paint(Display* display, Pixmap buffer) const {
    XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
}
void Paddle::moveXPos(int offset) {
    x += offset;
}
int Paddle::xPos() const {
    return x;
}
int Paddle::yPos() const {
    return y;
}
int Paddle::width() const {
    return widthDimension;
}
int Paddle::height() const {
    return heightDimension;
}
void Paddle::reset() {
    x = startingXPosition;
    y = startingYPosition;
}

/**********************************************************************/

Text::Text(int x, int y, GC gc, string text): x(x), y(y), gc(gc), text(text) {}
virtual void Text::paint(Display* display, Pixmap buffer) const {
    XDrawString(display, buffer, gc, x, y, text.c_str(), text.length());
}
void Text::update(string newText) {
    text = newText;
}
string Text::getText() const {
    return text;
}