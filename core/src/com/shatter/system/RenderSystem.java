package com.shatter.system;

import com.shatter.component.Fracture;
import com.shatter.component.Position;
import com.shatter.component.Visual;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderSystem extends IteratingSystem {
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	private ComponentMapper<Visual> sm = ComponentMapper.getFor(Visual.class);
	private ComponentMapper<Fracture> fm = ComponentMapper.getFor(Fracture.class);
	private ShapeRenderer shapeRenderer;
	private Camera camera;

	public RenderSystem(OrthographicCamera camera) {
		super(Family.all(Position.class,Visual.class).get());
		this.camera = camera;
		this.shapeRenderer = new ShapeRenderer();
	}

	protected void processEntity(Entity entity, float deltaTime) {
		Position pose = (Position) this.pm.get(entity);
		Visual shape = (Visual) this.sm.get(entity);
		Fracture fract = this.fm.get(entity);

		if (pose.pos.x > camera.viewportWidth / 2) {
			pose.pos.x = -camera.viewportWidth / 2;
		}
		if (pose.pos.y > camera.viewportHeight / 2) {
			pose.pos.y = -camera.viewportHeight / 2;
		}
		if (pose.pos.x < -camera.viewportWidth / 2) {
			pose.pos.x = camera.viewportWidth / 2;
		}
		if (pose.pos.y < -camera.viewportHeight / 2) {
			pose.pos.y = camera.viewportHeight / 2;
		}

		this.shapeRenderer.setColor(shape.COLOR);
		this.shapeRenderer.identity();
		this.shapeRenderer.translate(pose.pos.x, pose.pos.y, 0.0F);
		this.shapeRenderer.rotate(0.0F, 0.0F, 1.0F, pose.angle);
		this.shapeRenderer.polygon(shape.VERTICES);
		
		if(fract != null){
			for(int i = 0; i<fract.vd.size(); i++){
				this.shapeRenderer.polygon(fract.vd.get(i));
			}
			this.shapeRenderer.setColor(Color.GREEN);
			for(int i = 0; i<fract.dt.size(); i++){
				this.shapeRenderer.polygon(fract.dt.get(i).toVertexArray());
				//this.shapeRenderer.circle(fract.dt.get(i).getCcCenter().x, fract.dt.get(i).getCcCenter().y, fract.dt.get(i).getCcRadius());
			}
		}

	}

	public void update(float deltaTime) {
		this.shapeRenderer.setProjectionMatrix(this.camera.combined);
		this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		super.update(deltaTime);
		this.shapeRenderer.end();
	}
}
