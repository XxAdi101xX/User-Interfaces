#include <string>
#include <algorithm>
#include "components.h"

using namespace std;

/* Ball Class Definitions */
Ball::Ball(double xPos, double yPos, GC gc, int ballSize, double ballSpeed): 
            xPosition(xPos), yPosition(yPos), gc(gc), ballSize(ballSize),
            xDirection(ballSpeed), yDirection(-ballSpeed),
            startingXPosition(xPos), startingYPosition(yPos) {}
void Ball::paint(Display* display, Pixmap buffer) const {
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
void Ball::replaceGC(GC newGC) {
	gc = newGC;
}
void Ball::reset() {
    xPosition = startingXPosition;
    yPosition = startingYPosition;
    xDirection = abs(xDirection);
    yDirection = yDirection > 0 ? -yDirection : yDirection;
}

/* Block Class Definitions */

Block::Block(int x, int y, GC gc, int health): x(x), y(y), gc(gc),                                                                                        currentHealth(health), maxHealth(health) {}
void Block::paint(Display* display, Pixmap buffer) const {
    XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
}
int Block::xPos() const {
    return x;
}
int Block::yPos() const {
    return y;
}
int Block::width() const {
    return widthDimension;
}
int Block::height() const {
    return heightDimension;
}
void Block::onHit() {
    --currentHealth;
}
bool Block::isDestroyed() const {
    return currentHealth == 0;
}
void Block::reset() {
    currentHealth = maxHealth;
}

/* Paddle Class Defintions */
Paddle::Paddle(int x, int y, GC gc): x(x), y(y), startingXPosition(x), startingYPosition(y), gc(gc) {}
void Paddle::paint(Display* display, Pixmap buffer) const {
    XFillRectangle(display, buffer, gc, x, y, widthDimension, heightDimension);
}
void Paddle::moveXPos(int offset, int windowWidth) {
		int newPosition = x + offset;
		if (newPosition >= 0 && newPosition + widthDimension <= windowWidth) {
			x = newPosition;
		}
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

/* Text Class Definitions */
Text::Text(int x, int y, GC gc, string text): x(x), y(y), gc(gc), text(text) {}
void Text::paint(Display* display, Pixmap buffer) const {
    XDrawString(display, buffer, gc, x, y, text.c_str(), text.length());
}
void Text::update(string newText) {
    text = newText;
}
string Text::getText() const {
    return text;
}
